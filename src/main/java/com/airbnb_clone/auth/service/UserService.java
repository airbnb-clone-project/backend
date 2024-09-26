package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.oauth2.MoreUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.CustomUserDetails;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.dto.users.UsersProfileRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import com.airbnb_clone.auth.repository.SocialUserRepository;
import com.airbnb_clone.auth.repository.UserRepository;
import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.image.helper.S3UniqueKeyGenerator;
import com.airbnb_clone.image.validator.ContentTypeValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReissueService reissueService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final SocialUserRepository socialUserRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;


    @Transactional
    public ResponseEntity<?> register(UserRegisterRequest request, HttpServletResponse response) {

        String username = request.getUsername();
        String password = bCryptPasswordEncoder.encode(request.getPassword());


        // 유저정보 있을경우 예외처리
        if (!userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "이미 존재하는 사용자입니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
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


        // access token 생성
        String access = jwtUtil.createJwt("Authorization", username, userNo, 600000L);
        // refresh token 생성
        String refresh = jwtUtil.createJwt("refresh", username, userNo, 86400000L);
        // refresh token 저장
        reissueService.addRefreshToken(username, refresh, 86400000L);

        // header 에 access token 추가
        // cookie에 refresh token 담아 헤더에 추가
        response.setHeader("Authorization", access);
        response.addCookie(reissueService.createCookie("refresh", refresh));

        // 응답  body 생성

        ErrorResponse errorResponse = new ErrorResponse(200, "일반 회원가입이 완료 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    @Transactional
    public ResponseEntity<?> saveMoreUserInformation(MoreUserRegisterRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        request.setUsername(username);

        // 유저정보 없을경우 예외처리
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "없는 사용자입니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 입력 정보에 생일이 없고 db에 값이 있을경우 값이 바뀌면 안됨
        // 입력 생일 없음
        if (request.getBirthday() == null) {
            // db 생일 있음
            LocalDate birthday = userRepository.findBirthdayByUsername(username);
            if (birthday != null) {
                // 입력정보에 db 정보 업데이트
                request.setBirthday(birthday);
            }
        }

        userRepository.saveMoreUserInformation(request);

        ErrorResponse errorResponse = new ErrorResponse(200, "추가정보 등록이 완료 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    // 유저 비밀번호 변경
    @Transactional
    public ResponseEntity<?> changePassword(NewPasswordRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String oldPassword = request.getPassword();
        String newPassword = bCryptPasswordEncoder.encode(request.getNewPassword());

        if (username.equals("anonymousUser")) {
            ErrorResponse errorResponse = new ErrorResponse(401, "비밀번호 변경 실패 했습니다 ㅎㅎ.");
            return ResponseEntity
                    .status(401)
                    .body(errorResponse);
        }

        /*
            없을경우 그냥 업데이트(소셜유저인데 일반 로그인을 하지 않은 유저)
            request에 옛날 비밀번호가 있을경우 비교 후 비밀번호 문제 없는지 확인 후 업데이트
         */
        String oldPasswordInDb = userRepository.findOldPasswordByUsername(username);
        if (oldPasswordInDb == null) {
            userRepository.updatePassword(username, newPassword);
        } else if (bCryptPasswordEncoder.matches(oldPassword, oldPasswordInDb)) {
            userRepository.updatePassword(username, newPassword);
        } else {
            ErrorResponse errorResponse = new ErrorResponse(401, "비밀번호 변경 실패 했습니다.");
            return ResponseEntity
                    .status(401)
                    .body(errorResponse);
        }

        ErrorResponse errorResponse = new ErrorResponse(200, "비밀번호 변경 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    public ResponseEntity<?> eraseAccounts(HttpServletRequest request) {
        System.out.println("UserService.eraseAccounts");
        // 쿠키에서 refresh token 을 가져온다.
        String givenToken = null;

        // 리프레시 토큰이 있는지 확인
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 이 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 쿠키가 있으니 refresh 가 있을경우 givenToken 에 추가
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                givenToken = cookie.getValue();
            }
        }

        /*
            리프레시 토큰 없음
            status : 401 , message : refresh token 이 없습니다.
         */
        if (givenToken == null) {
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 의 값이 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            expired check
            status : 401 , message : 리프레시 토큰이 만료되었습니다.
         */
        if (jwtUtil.isExpired(givenToken)) {
            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "리프레시 토큰이 만료되었습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }


        /*
            토큰이 refresh token 이 맞는지 확인
            status : 401 , message : refresh token이 아닙니다.
         */
        String tokenType = jwtUtil.getTokenType(givenToken);
        if (!tokenType.equals("refresh")) {

            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 이 아닙니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            DB를 조회해 refresh token 대조. 없으면 메시지
            status : 401 , message : 인증이 불가능한 토큰 입니다.
         */
        Boolean isNotExist = refreshTokenRepository.existsByRefresh(givenToken);
        if (isNotExist) {

            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "인증이 불가능한 토큰 입니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 토큰에 문제가 없을 때 실행
        String username = jwtUtil.getUsername(givenToken);
        Long userNo = userRepository.findNoByUsername(username);
        System.out.println("userNo = " + userNo);

        // refresh_token 삭제
        refreshTokenRepository.deleteRefreshToken(givenToken);
        // users 삭제
        userRepository.deleteAccount(username);
        // social_user 삭제
        if (userNo != null) {
            System.out.println("userNo = " + userNo);
            socialUserRepository.deleteSocialAccount(userNo);
        }



        ErrorResponse errorResponse = new ErrorResponse(200, "토큰이 재발급 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    // access token 입력
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // access token에서 userNo 가져오는 예
//        String token = request.getHeader("Authorization");
//        String modifiedToken = token.substring(7);
//        Long userNo= jwtUtil.getUserNo(modifiedToken);


        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "일치하는 유저 정보가 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        Users users = userRepository.findProfileByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "아이디를 찾을 수 없습니다."));

        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("message", "프로필 정보 불러오기 성공 했습니다.");
        jsonBody.put("status", 200);

        Map<String, String> data = new HashMap<>();
        data.put("description", users.getDescription());
        data.put("firstName", users.getFirstName());
        data.put("lastName", users.getLastName());
        jsonBody.put("data", data);

        return ResponseEntity
                .ok()
                .body(jsonBody);
    }

    // access/json
    public ResponseEntity<?> setProfile(HttpServletRequest request, UsersProfileRequest usersProfileRequest) {


        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "일치하는 유저 정보가 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // dto에 유저이름 추가
        usersProfileRequest.setUsername(username);

        // repository를 통해 저장
        userRepository.updateUserProfile(usersProfileRequest);

        ErrorResponse errorResponse = new ErrorResponse(200, "유저 프로필 정보 업데이트 했습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    // 이메일 생일 성별 국가 언어
    public ResponseEntity<?> getAccount(HttpServletRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "일치하는 유저 정보가 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        Users users = userRepository.findAccountByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "아이디를 찾을 수 없습니다."));

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

        return ResponseEntity
                .ok()
                .body(jsonBody);
    }


    /*
        api를 만들지 않는 methods
     */

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
