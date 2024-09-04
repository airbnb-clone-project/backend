package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.oauth2.MoreUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;


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
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReissueService reissueService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     * @param request username, password, birthday
     */
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

        // access token 생성
        String access = jwtUtil.createJwt("Authorization", username, 600000L);
        // refresh token 생성
        String refresh = jwtUtil.createJwt("refresh", username, 86400000L);
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
        if (request.getBirthday()==null) {
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


    //---- api를 만들지 않는 methods

    // username(email)에서 first name 생성
    public String makeFirstName(String username) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < username.length(); i++) {
            char currentChar = username.charAt(i);
            if (Character.isDigit(currentChar) || (currentChar=='@') ||(currentChar=='.')) {
                break;
            }
            if (Character.isLetter(currentChar)) {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    // username(email)에서 last name 생성
    public String makeLastName(String username) {
        StringBuilder result = new StringBuilder();
        boolean lastNameExist = false;

        for (int i = 0; i < username.length(); i++) {
            char currentChar = username.charAt(i);
            if (Character.isDigit(currentChar) || currentChar=='@') {
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
