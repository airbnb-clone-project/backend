package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.controller.UserController;
import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.oauth2.MoreUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.apache.catalina.security.SecurityUtil;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
 * 2024. 9. 6.        doungukkim       유저 추가정보 입력 테스트 코드 추가
 *
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
    String newPassword = "4321";
    String encodedOldOne = "encoded1234";
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
    @DisplayName("이메일 회원가입 - 실패(회원 중복)")
    void registerFailure() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("test@test2.com");
        request.setPassword("2222");

        when(userRepository.isUsernameNotExist("test@test2.com")).thenReturn(false);

        // when
        ResponseEntity<?> result = userService.register(request, response);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("이미 존재하는 사용자입니다.", body.getMessage());
    }

    @Test
    @DisplayName("유저 추가정보 입력 - 성공")
    void addMoreUserInfoSuccess() {

        MoreUserRegisterRequest request = new MoreUserRegisterRequest();
        request.setBirthday(LocalDate.of(2000, 4, 29));
        request.setCountry("Korea");
        request.setGender("assert helicopter");
        request.setSpokenLanguage("Korean");


        // username = SercurityContext.getContext().getAuthentication().getName()
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.isUsernameNotExist(username)).thenReturn(false);

        ResponseEntity<?> result = userService.saveMoreUserInformation(request);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("추가정보 등록이 완료 되었습니다.", body.getMessage());
        verify(userRepository, times(1)).saveMoreUserInformation(request);
    }

    @Test
    @DisplayName("유저 추가정보 입력 - 실패(유저정보 없음)")
    void addMoreUserInfoFail() {
        String wrongUsername = "wrong@test.com";

        MoreUserRegisterRequest request = new MoreUserRegisterRequest();
        request.setBirthday(LocalDate.of(2000, 4, 29));
        request.setCountry("Korea");
        request.setGender("assert helicopter");
        request.setSpokenLanguage("Korean");


        // username = SercurityContext.getContext().getAuthentication().getName()
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(wrongUsername);

        SecurityContextHolder.setContext(securityContext);

        // 유저가 있을경우 jwt를 조회해 찾은 결과는 있어야
        when(userRepository.isUsernameNotExist(wrongUsername)).thenReturn(true);

        ResponseEntity<?> result = userService.saveMoreUserInformation(request);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("없는 사용자입니다.", body.getMessage());
        // 조회 없어야됨
        verify(userRepository, times(0)).saveMoreUserInformation(any());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공(기존 비밀번호 없음)")
    void changePasswordSuccess2() {

        NewPasswordRequest request = new NewPasswordRequest();
        request.setPassword(oldPassword);
        request.setNewPassword(newPassword);
        request.setUsername(username);

        // SecurityContext와 Authentication 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);


        when(userRepository.findOldPasswordByUsername(username)).thenReturn(null);
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedNewOne);

        // when
        ResponseEntity<?> result = userService.changePassword(request);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("비밀번호 변경 되었습니다.", body.getMessage());
        verify(userRepository, times(1)).updatePassword(username, encodedNewOne);

    }

    @Test
    @DisplayName("비밀번호 변경 - 성공(기존 비밀번호 있음)")
    void changePasswordSuccess() {

        NewPasswordRequest newPasswordRequest = new NewPasswordRequest();
        newPasswordRequest.setPassword(oldPassword);
        newPasswordRequest.setNewPassword(newPassword);
        newPasswordRequest.setUsername(username);

        // SecurityContext와 Authentication 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

        // 바꾸기 전
        when(userRepository.findOldPasswordByUsername(username)).thenReturn(encodedOldOne);
        // 옛날 비밀번호 정확히 입력 했는지 확인
        when(bCryptPasswordEncoder.matches(oldPassword, encodedOldOne)).thenReturn(true);

        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedNewOne);

        // when
        ResponseEntity<?> changePasswordResult = userService.changePassword(newPasswordRequest);
        // 바꾼 후
        when(userRepository.findOldPasswordByUsername(username)).thenReturn(encodedNewOne);

        // Then
        assertEquals(changePasswordResult.getStatusCode(), HttpStatus.OK);
        assertEquals(encodedNewOne, userRepository.findOldPasswordByUsername(username));


        // response body 확인, 업데이트 1회 사용, 사용시 username과 encodedNewOne 사용
        ErrorResponse body = (ErrorResponse) changePasswordResult.getBody();
        assertNotNull(body);
        assertEquals(body.getMessage(), "비밀번호 변경 되었습니다.");
        assertEquals(body.getStatus(), 200);
        verify(userRepository, times(1)).updatePassword(username, encodedNewOne);

    }

    @Test
    @DisplayName("비밀번호 변경 - 실패(틀린 기존 비밀번호 입력)")
    void changePasswordFailure() {

        NewPasswordRequest request = new NewPasswordRequest();
        request.setUsername(username);
        request.setPassword(oldPassword);
        request.setNewPassword(newPassword);

        // SecurityContext와 Authentication 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

        // db 안의 유저 비밀번호 조회시 -> dbPassword
        when(userRepository.findOldPasswordByUsername(username)).thenReturn("dbPassword");

        String dbPassword = userRepository.findOldPasswordByUsername(username);

        // 유저가 입력한 비밀번호와 저장되어있던 비밀번호 비교시 -> false(다르다)
        when(bCryptPasswordEncoder.matches(oldPassword, dbPassword)).thenReturn(false);

        ResponseEntity<?> result = userService.changePassword(request);

        // body 확인, 업데이트 함수 사용했는지 확인
        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("비밀번호 변경 실패 했습니다.",body.getMessage());
        verify(userRepository, never()).updatePassword(anyString(), anyString());
    }
}