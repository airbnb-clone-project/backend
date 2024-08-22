package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * packageName    : package com.airbnb_clone.auth.service;
 * fileName       : UserService
 * author         : DK
 * date           : 24. 8. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;


    /**
     * 회원가입
     * @param request username, password, birthday
     */
    public ResponseEntity<?> register(UserRegisterRequest request) {

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
                .gender(request.getGender())
                .spokenLanguage(request.getSpokenLanguage())
                .country(request.getCountry())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        userRepository.registerUser(user);

        ErrorResponse errorResponse = new ErrorResponse(200, "일반 회원가입이 완료 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }


    // username(email)에서 first name 생성
    public String makeFirstName(String username) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < username.length(); i++) {
            char currentChar = username.charAt(i);
            if (Character.isDigit(currentChar) || (currentChar=='@')) {
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
