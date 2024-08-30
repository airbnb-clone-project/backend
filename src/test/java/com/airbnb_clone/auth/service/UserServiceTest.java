package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * packageName    : com.airbnb_clone.auth.service
 * fileName       : UserServiceTest
 * author         : doungukkim
 * date           : 2024. 8. 31.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 31.        doungukkim       최초 생성
 */
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    public BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ReissueService reissueService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() {
//        UserRegisterRequest request = new UserRegisterRequest();
//        request.setUsername("test5478@test.com");
//        request.setPassword("1234");
//
//        // "when 내가 가입할 이메일이 없는 상태"
//        when(userRepository.isUsernameNotExist("test5478@test.com")).thenReturn(true);

        // 생일 포함 저장
        // 생일 미포함 저장
        // 이메일 중복 가입
        // 암호화 되는지 확인
        // 소셜 로그인시 일반 회원가입 불가능 여부
    }

    @Test
    void saveMoreUserInformation() {
        // 유저 없는데 추가정보를 넣을 경우
        // 생일 값이 있을 경우
        // 생일 값이 없을 경우
    }

    @Test
    void changePassword() {
// 비밀번호 없는 유저 추가
//        UserRegisterRequest request = new UserRegisterRequest();
//        request.setUsername("test5478@test.com");
//        Users users = Users.builder()
//                .username(request.getUsername())
//                .password(null)
//                .build();
//        userRepository.registerUser(users);
//
//        String oldPassword = userRepository.findOldPasswordByUsername(users.getUsername());
//        System.out.println("Old password: " + oldPassword);
//
//        // 비밀번호 수정 요청
//        NewPasswordRequest passwordRequest = new NewPasswordRequest();
//        passwordRequest.setUsername("test5478@test.com");
//        passwordRequest.setNewPassword("new");
//        System.out.println("New password set: " + passwordRequest.getNewPassword());
//
//        userService.changePassword(passwordRequest);
//
//        String newPassword = userRepository.findOldPasswordByUsername(users.getUsername());
//        System.out.println("Updated password: " + newPassword);
//
//        Assertions.assertThat(bCryptPasswordEncoder.matches("new", newPassword)).isEqualTo(true);

//        // DB에 비밀번호가 없는 경우 -> 업데이트(o)
//
//        // 비밀번호 있는 유저 추가

        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("test5478@test.com");
        request.setPassword("old");
        System.out.println("request.getPassword() = " + request.getPassword());


        userService.register(request, response);

        System.out.println("userRepository = " + userRepository.findByUsername("test5478@test.com"));





        // 비밀번호 수정 요청
//        NewPasswordRequest passwordRequest = new NewPasswordRequest();
//        // 비밀번호 있는 유저
//        passwordRequest.setUsername("test5478@test.com");
//        passwordRequest.setUsername("old");
//        passwordRequest.setNewPassword("new");
//
//        userService.changePassword(passwordRequest);
//
//        String newPassword = userRepository.findOldPasswordByUsername(users.getUsername());

//        Assertions.assertThat(bCryptPasswordEncoder.matches("new", newPassword)).isEqualTo(true);



//        Users users = Users.builder()
//                .username(request.getUsername())
//                .password(request.getPassword())
//                .build();
//
//        // 비밀번호 없는 유저
//        UserRegisterRequest request2 = new UserRegisterRequest();
//        request2.setUsername("test5478@test.com");
//        request2.setPassword("1234");
//        Users users = Users.builder()
//                .username(request2.getUsername())
//                .password(request2.getPassword())
//                .build();
//
//        userRepository.registerUser(users);
        // DB에 비밀번호가 없는 경우 -> 업데이트(o)
        // DB에 비밀번호가 있는 경우 옛날 비밀번호 일치 -> 업데이트(o)
        // DB에 비밀번호가 있는 경우 옛날 비밀번호 불일치 -> 업데이트(x)

    }

    @Test
    void makeFirstName() {
        // 알파벳만 있는 경우
        // 알파뱃과 숫자가 있는 경우
        // 알파뱃과 .이 있는 경우

    }

    @Test
    void makeLastName() {
        // 알파벳만 있는 경우
        // 알파뱃과 숫자가 있는 경우
        // 알파뱃과 .이 있는 경우
    }
}