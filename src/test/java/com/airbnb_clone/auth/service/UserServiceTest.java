package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.controller.UserController;
import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ReissueService reissueService;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        response = new MockHttpServletResponse();
    }

    String username = "test@test.com";
    String oldPassword = "1234";
    String encodedOldOne = "encoded1234";
    String newPassword = "4321";
    String encodedNewOne = "encoded4321";
    @Test
    @DisplayName("이메일 회원가입 -성공")
    void registerSuccess() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("test@test.com");
        request.setPassword("1234");

        // Mock
        // test@test.com 없음 -> true
        when(userRepository.isUsernameNotExist("test@test.com")).thenReturn(true);
        // 1234를 인코딩 할 경우 -> endocded1234
        when(bCryptPasswordEncoder.encode("1234")).thenReturn("encoded1234");
        // access 생성시 Authorization=accessToken
        when(jwtUtil.createJwt(eq("Authorization"), eq("test@test.com"), anyLong())).thenReturn("accessToken");
        // refresh 생성시 refresh=refreshToken
        when(jwtUtil.createJwt(eq("refresh"), eq("test@test.com"), anyLong())).thenReturn("refreshToken");
        // Mock the createCookie method
        // refresh는 쿠키 안에 담겨 있어야 함
        Cookie mockCookie = new Cookie("refresh", "refreshToken");
        when(reissueService.createCookie(eq("refresh"), eq("refreshToken"))).thenReturn(mockCookie);

        // When
        ResponseEntity<?> result = userService.register(request, response);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userRepository).registerUser(any(Users.class));
        verify(reissueService).addRefreshToken(eq("test@test.com"), eq("refreshToken"), anyLong());
        assertEquals("accessToken", response.getHeader("Authorization"));
        assertNotNull(response.getCookie("refresh"));

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(body.getStatus(), 200);
        assertEquals(body.getMessage(), "일반 회원가입이 완료 되었습니다.");
    }


    @Test
    @DisplayName("이메일 회원가입 실패 - 회원 중복")
    void registerFailure() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("test@test2.com");
        request.setPassword("2222");

        when(userRepository.isUsernameNotExist("test@test2.com")).thenReturn(false);

        ResponseEntity<?> result = userService.register(request, response);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("이미 존재하는 사용자입니다.", body.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void changePasswordSuccess() {

        NewPasswordRequest newPasswordRequest = new NewPasswordRequest();
        newPasswordRequest.setPassword(oldPassword);
        newPasswordRequest.setNewPassword(newPassword);
        newPasswordRequest.setUsername(username);

        // 바꾸기 전
        when(userRepository.findOldPasswordByUsername(username)).thenReturn(encodedOldOne);
        // 옛날 비밀번호 정확히 입력 했는지 확인
        when(bCryptPasswordEncoder.matches(oldPassword, encodedOldOne)).thenReturn(true);

        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedNewOne);

        ResponseEntity<?> changePasswordResult = userService.changePassword(newPasswordRequest);

        // 바꾼 후
        when(userRepository.findOldPasswordByUsername(username)).thenReturn(encodedNewOne);

        // Then
        assertEquals(changePasswordResult.getStatusCode(), HttpStatus.OK);
        assertEquals(encodedNewOne, userRepository.findOldPasswordByUsername(username));


        ErrorResponse body = (ErrorResponse) changePasswordResult.getBody();
        assertNotNull(body);
        assertEquals(body.getMessage(), "비밀번호 변경 되었습니다.");
        assertEquals(body.getStatus(),200);

    }

    @Test
    @DisplayName("비밀번호 변경 - 실패")
    void changePasswordFailure() {
        UserRegisterRequest request3 = new UserRegisterRequest();
        request3.setUsername("test@test.com");
        request3.setPassword("1234");

        when(userRepository.isUsernameNotExist("test@test.com")).thenReturn(true);
        // 1234를 인코딩 할 경우 -> endocded1234
        when(bCryptPasswordEncoder.encode("1234")).thenReturn("encoded1234");


    }
}