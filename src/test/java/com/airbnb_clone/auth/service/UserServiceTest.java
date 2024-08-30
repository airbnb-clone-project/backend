package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
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
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("test5478@test.com");
        request.setPassword("1234");

        // "when 내가 가입할 이메일이 없는 상태"
        when(userRepository.isUsernameNotExist("test5478ㅇ@test.com")).thenReturn(true);

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
        // 비밀번호가 없는경우
        // 비밀번호가 있는경우
        // 이전 비밀번호가 일치하지 않음
        // 이전 비밀번호가 일치함
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