package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.oauth2.AdditionalUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.dto.users.UsersProfileRequest;
import com.airbnb_clone.auth.jwt.TokenUtil;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import com.airbnb_clone.auth.repository.SocialUserRepository;
import com.airbnb_clone.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * packageName    : package com.airbnb_clone.auth.service;
 * fileName       : UserService
 * author         : DK
 * date           : 24. 8. 22.
 * description    : 회원가입, 회원 추가정보 저장 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 * 24. 8. 30.        DK       계정 생성시 acc/ref token 생성
 * 24. 8. 31.        DK       계정 추가정보 업데이트 기능 추가
 * 24. 9. 04.        DK       추가정보, 비밀번호 변경 username 입력 받지 않게 변경
 * 24. 9. 13.        DK       profile 가져오는 기능 추가
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReissueService reissueService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final SocialUserRepository socialUserRepository;
    private final TokenUtil tokenUtil;

    private static final String ACCESS_TOKEN_NAME = "Authorization";
    private static final String REFRESH_TOKEN_NAME = "refresh";



    @Transactional
    public ResponseEntity<?> registerUser(UserRegisterRequest request, HttpServletResponse response) {

        String username = request.getUsername();
        String password = bCryptPasswordEncoder.encode(request.getPassword());


        // 유저정보 있을경우 예외처리
        if (userRepository.isUsernameExist(username)) {
            return new ErrorResponse().ofUnauthorized("이미 존재하는 사용자입니다.");
        }

        // build 방식 유저 정보 저장을 위한 user 생성
        Users user = Users.builder()
                .username(username)
                .password(password)
                .firstName(makeFirstName(username))
                .lastName(makeLastName(username))
                .birthday(request.getBirthday())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        userRepository.registerUser(user);

        // 토큰 생성, 헤더 추가
        Long userNo = userRepository.findNoByUsername(username);


        // access, refresh token 생성
        String access = jwtUtil.createJwt(ACCESS_TOKEN_NAME, username, userNo, 600000L);
        String refresh = jwtUtil.createJwt(REFRESH_TOKEN_NAME, username, userNo, 86400000L);
        // refresh token 저장
        reissueService.addRefreshToken(username, refresh, 86400000L);

        // header 에 access token 추가
        // cookie에 refresh token 담아 헤더에 추가
        tokenUtil.addAccessInHeader(response, access);
        tokenUtil.addRefreshInCookie(response, refresh);

        // 응답  body 생성
        return new ErrorResponse().ofSuccessBody("일반 회원가입이 완료 되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> saveAdditionalUserInformation(AdditionalUserRegisterRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        request.setUsername(username);

        // 유저정보 없을경우 예외처리
        if (userRepository.isUsernameNotExist(username)) {
            return new ErrorResponse().ofUnauthorized("없는 사용자입니다.");

        }

        // 입력 정보에 생일이 없고 db에 값이 있을경우 값이 바뀌면 안됨
        // 입력 생일 없음

        if (doesNotHaveBirthday(request)) {
            // db 생일 있음 db에서 가져온다.
            LocalDate birthdayFromDb = userRepository.findBirthdayByUsername(username);
            if (doesHaveBirthday(birthdayFromDb)) {
                // 입력정보에 db 정보 업데이트
                request.setBirthday(birthdayFromDb);
            }
        }

        userRepository.saveAdditionalUserInformation(request);

        return new ErrorResponse().ofSuccessBody("추가정보 등록이 완료 되었습니다.");
    }

    // 유저 비밀번호 변경
    @Transactional
    public ResponseEntity<?> changePassword(NewPasswordRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String oldPassword = request.getPassword();
        String newPassword = bCryptPasswordEncoder.encode(request.getNewPassword());

        ErrorResponse errorResponse = new ErrorResponse();

        if (isAnonymousUser(username)) {
            return errorResponse.ofUnauthorized("비밀번호 변경 실패 했습니다 ㅎㅎ.");
        }

        /*
            없을경우 그냥 업데이트(소셜유저인데 일반 로그인을 하지 않은 유저)
            request에 옛날 비밀번호가 있을경우 비교 후 비밀번호 문제 없는지 확인 후 업데이트
         */
        String oldPasswordFromDb = userRepository.findCurrnetPasswordByUsername(username);

        // 비번 아직 없을 때 - 저장
        if (oldPasswordNotExist(oldPasswordFromDb)) {
            userRepository.updatePassword(username, newPassword);
        }
        // 변경하려는 비번과 입력 인풋이 같을 때 - 저장
        else if (inputOldPasswordMatchedPasswordFromDb(oldPassword, oldPasswordFromDb)) {
            userRepository.updatePassword(username, newPassword);
        }
        // 그 외엔 변경 실패
        else {

            return errorResponse.ofUnauthorized("비밀번호 변경 실패 했습니다.");
        }

        return errorResponse.ofSuccessBody("비밀번호 변경 되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> eraseAccounts(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        // 쿠키에서 refresh token 을 가져온다.
        String givenToken = null;

        // 리프레시 토큰이 있는지 확인
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return errorResponse.ofUnauthorized("refresh token 이 없습니다.");
        }

        // 쿠키가 있으니 refresh 가 있을경우 givenToken 에 추가
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_NAME)) {
                givenToken = cookie.getValue();
            }
        }

        /*
            리프레시 토큰 없음
            status : 401 , message : refresh token 이 없습니다.
         */
        if (givenToken == null) {
            return errorResponse.ofUnauthorized("refresh token 의 값이 없습니다.");
        }

        /*
            expired check
            status : 401 , message : 리프레시 토큰이 만료되었습니다.
         */
        if (jwtUtil.isExpired(givenToken)) {
            // response status code
            return errorResponse.ofUnauthorized("리프레시 토큰이 만료되었습니다.");
        }


        /*
            토큰이 refresh token 이 맞는지 확인
            status : 401 , message : refresh token이 아닙니다.
         */
        String tokenType = jwtUtil.getTokenType(givenToken);
        if (!tokenType.equals(REFRESH_TOKEN_NAME)) {

            // response status code
            return errorResponse.ofUnauthorized("refresh token 이 아닙니다.");
        }

        /*
            DB를 조회해 refresh token 대조. 없으면 메시지
            status : 401 , message : 인증이 불가능한 토큰 입니다.
         */
        Boolean isNotExist = refreshTokenRepository.existsByRefresh(givenToken);
        if (isNotExist) {

            // response status code
            return errorResponse.ofUnauthorized("인증이 불가능한 토큰 입니다.");
        }

        // 토큰에 문제가 없을 때 실행
        String username = jwtUtil.getUsername(givenToken);
        Long userNo = userRepository.findNoByUsername(username);
        System.out.println("userNo = " + userNo);

        // refresh_token 삭제
        refreshTokenRepository.deleteRefreshToken(givenToken);
        // users 삭제
        userRepository.deleteUser(username);
        // social_user 삭제
        if (userNo != null) {
            System.out.println("userNo = " + userNo);
            socialUserRepository.deleteSocialAccount(userNo);
        }

        return new ErrorResponse().ofSuccessBody("토큰이 재발급 되었습니다.");
    }

    // access token 입력
    public ResponseEntity<?> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ErrorResponse errorResponse = new ErrorResponse();
        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            return errorResponse.ofUnauthorized("일치하는 유저 정보가 없습니다.");
        }

        Users users = userRepository.findProfileByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "아이디를 찾을 수 없습니다."));

        Map<String, Object> jsonBody = getJsonProfileOutput(users);

        return ResponseEntity
                .ok()
                .body(jsonBody);
    }

    // access/json
    @Transactional
    public ResponseEntity<?> setProfile(UsersProfileRequest usersProfileRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ErrorResponse errorResponse = new ErrorResponse();

        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            return errorResponse.ofUnauthorized("일치하는 유저 정보가 없습니다.");
        }

        // dto에 유저이름 추가
        usersProfileRequest.setUsername(username);

        // repository를 통해 저장
        userRepository.updateUserProfile(usersProfileRequest);

        return new ErrorResponse().ofSuccessBody("유저 프로필 정보 업데이트 했습니다.");
    }

    // 이메일 생일 성별 국가 언어
    public ResponseEntity<?> getAccount() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "일치하는 유저 정보가 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        Users users = userRepository.findAccountByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "아이디를 찾을 수 없습니다."));

        Map<String, Object> jsonBody = getJsonAccountOutPut(users);

        return ResponseEntity
                .ok()
                .body(jsonBody);
    }

    private static boolean doesHaveBirthday(LocalDate birthday) {
        return birthday != null;
    }

    private static boolean doesNotHaveBirthday(AdditionalUserRegisterRequest request) {
        return request.getBirthday() == null;
    }

    private boolean inputOldPasswordMatchedPasswordFromDb(String oldPassword, String oldPasswordFromDb) {
        return bCryptPasswordEncoder.matches(oldPassword, oldPasswordFromDb);
    }

    private static boolean oldPasswordNotExist(String oldPasswordFromDb) {
        return oldPasswordFromDb == null;
    }

    private static boolean isAnonymousUser(String username) {
        return username.equals("anonymousUser");
    }

    private static Map<String, Object> getJsonProfileOutput(Users users) {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("message", "프로필 정보 불러오기 성공 했습니다.");
        jsonBody.put("status", 200);

        Map<String, String> data = new HashMap<>();
        data.put("description", users.getDescription());
        data.put("firstName", users.getFirstName());
        data.put("lastName", users.getLastName());
        jsonBody.put("data", data);
        return jsonBody;
    }

    private static Map<String, Object> getJsonAccountOutPut(Users users) {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("message", "계정 정보 불러오기 성공 했습니다.");
        jsonBody.put("status", 200);

        // 이메일 생일 성별 국가 언어
        Map<String, String> data = new HashMap<>();
        data.put("username", users.getUsername());
        data.put("birthday", users.getBirthday().toString());
        data.put("gender", users.getGender());
        data.put("spokenLanguage", users.getSpokenLanguage());
        jsonBody.put("data", data);
        return jsonBody;
    }

    // username(email)에서 first name 생성
    private String makeFirstName(String username) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < username.length(); i++) {
            char currentChar = username.charAt(i);
            if (Character.isDigit(currentChar) || (currentChar == '@') || (currentChar == '.')) {
                break;
            }
            if (Character.isLetter(currentChar)) {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    // username(email)에서 last name 생성
    private String makeLastName(String username) {
        StringBuilder result = new StringBuilder();
        boolean lastNameExist = false;

        for (int i = 0; i < username.length(); i++) {
            char currentChar = username.charAt(i);
            if (Character.isDigit(currentChar) || currentChar == '@') {
                break;
            }

            if (currentChar == '.') {
                result.setLength(0);
                lastNameExist = true;
            }

            if (Character.isLetter(currentChar)) {
                result.append(currentChar);
            }
        }
        if (lastNameExist) {
            return result.toString();
        } else {
            return "";
        }
    }
}
