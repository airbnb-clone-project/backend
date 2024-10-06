package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.oauth2.AdditionalUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.dto.users.UsersProfileRequest;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.jwt.TokenUtil;
import com.airbnb_clone.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

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
 * 2024. 9. 13.        doungukkim       profile 불러오는 성공 test code 추가
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
    private TokenUtil tokenUtil;

    @Mock
    private ReissueService reissueService;

    private MockHttpServletResponse response;
    @Mock
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        response = new MockHttpServletResponse();
    }

    static final String USERNAME = "test@test.com";
    static final String PASSWORD = "1234";
    static final String NEW_PASSWORD = "4321";
    static final String ENCODED_OLD_ONE = "encoded1234";
    static final String ENCODED_NEW_ONE = "encoded4321";
    static final String ACCESS_TOKEN_NAME = "Authorization";
    static final String REFRESH_TOKEN_NAME = "refresh";
    static final String ACCESS_TOKEN_VALUE = "accessToken";
    static final String REFRESH_TOKEN_VALUE = "refreshToken";

    @Test
    @DisplayName("이메일 회원가입 -성공")
    void registerSuccess() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        // Mock
        // test@test.com 없음 -> true
        when(userRepository.isUsernameExist("test@test.com")).thenReturn(false);
        // 1234를 인코딩 할 경우 -> endocded1234
        when(bCryptPasswordEncoder.encode("1234")).thenReturn("encoded1234");
        // access 생성시 Authorization=accessToken
        when(jwtUtil.createJwt(eq(ACCESS_TOKEN_NAME), eq(USERNAME),anyLong(), anyLong())).thenReturn(ACCESS_TOKEN_VALUE);
        // refresh 생성시 refresh=refreshToken
        when(jwtUtil.createJwt(eq(REFRESH_TOKEN_NAME), eq(USERNAME), anyLong(), anyLong())).thenReturn(REFRESH_TOKEN_VALUE);

        // When
        ResponseEntity<?> result = userService.registerUser(request, response);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userRepository).registerUser(any(Users.class));
        verify(reissueService).addRefreshToken(eq(USERNAME), eq(REFRESH_TOKEN_VALUE), anyLong());
        verify(tokenUtil).addAccessInHeader(response, ACCESS_TOKEN_VALUE);
        verify(tokenUtil).addRefreshInCookie(response, REFRESH_TOKEN_VALUE);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(body.getStatus(), 200);
        assertEquals(body.getMessage(), "일반 회원가입이 완료 되었습니다.");
    }


    @Test
    @DisplayName("이메일 회원가입 - 실패(회원 중복)")
    void registerFailure() {

        // given
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        when(userRepository.isUsernameExist(USERNAME)).thenReturn(true);

        // when
        ResponseEntity<?> result = userService.registerUser(request, response);

        // then
        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("이미 존재하는 사용자입니다.", body.getMessage());
    }

    @Test
    @DisplayName("유저 추가정보 입력 - 성공")
    void addMoreUserInfoSuccess() {

        AdditionalUserRegisterRequest request = new AdditionalUserRegisterRequest();
        request.setBirthday(LocalDate.of(2000, 4, 29));
        request.setCountry("Korea");
        request.setGender("assert helicopter");
        request.setSpokenLanguage("Korean");

        // username = SercurityContext.getContext().getAuthentication().getName()
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.isUsernameNotExist(USERNAME)).thenReturn(false);

        // when
        ResponseEntity<?> result = userService.saveAdditionalUserInformation(request);

        // then
        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("추가정보 등록이 완료 되었습니다.", body.getMessage());
        verify(userRepository, times(1)).saveAdditionalUserInformation(request);
    }

    @Test
    @DisplayName("유저 추가정보 입력 - 실패(유저정보 없음)")
    void addMoreUserInfoFail() {
        // given
        String wrongUsername = "wrong@test.com";

        AdditionalUserRegisterRequest request = new AdditionalUserRegisterRequest();
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

        // when
        ResponseEntity<?> result = userService.saveAdditionalUserInformation(request);

        // then
        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("없는 사용자입니다.", body.getMessage());
        // 조회 없어야됨
        verify(userRepository, times(0)).saveAdditionalUserInformation(any());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공(기존 비밀번호 없음)")
    void changePasswordSuccess2() {

        NewPasswordRequest request = new NewPasswordRequest();
        request.setPassword(PASSWORD);
        request.setNewPassword(NEW_PASSWORD);


        // SecurityContext와 Authentication 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);


        when(userRepository.findCurrnetPasswordByUsername(USERNAME)).thenReturn(null);
        when(bCryptPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_ONE);

        // when
        ResponseEntity<?> result = userService.changePassword(request);

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("비밀번호 변경 되었습니다.", body.getMessage());
        verify(userRepository, times(1)).updatePassword(USERNAME, ENCODED_NEW_ONE);

    }

    @Test
    @DisplayName("비밀번호 변경 - 성공(기존 비밀번호 있음)")
    void changePasswordSuccess() {

        NewPasswordRequest newPasswordRequest = new NewPasswordRequest();
        newPasswordRequest.setPassword(PASSWORD);
        newPasswordRequest.setNewPassword(NEW_PASSWORD);

        // SecurityContext와 Authentication 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

        // 바꾸기 전
        when(userRepository.findCurrnetPasswordByUsername(USERNAME)).thenReturn(ENCODED_OLD_ONE);
        // 옛날 비밀번호 정확히 입력 했는지 확인
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCODED_OLD_ONE)).thenReturn(true);

        when(bCryptPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_ONE);

        // when
        ResponseEntity<?> changePasswordResult = userService.changePassword(newPasswordRequest);
        // 바꾼 후
        when(userRepository.findCurrnetPasswordByUsername(USERNAME)).thenReturn(ENCODED_NEW_ONE);

        // Then
        assertEquals(changePasswordResult.getStatusCode(), HttpStatus.OK);
        assertEquals(ENCODED_NEW_ONE, userRepository.findCurrnetPasswordByUsername(USERNAME));


        // response body 확인, 업데이트 1회 사용, 사용시 username과 encodedNewOne 사용
        ErrorResponse body = (ErrorResponse) changePasswordResult.getBody();
        assertNotNull(body);
        assertEquals(body.getMessage(), "비밀번호 변경 되었습니다.");
        assertEquals(body.getStatus(), 200);
        verify(userRepository, times(1)).updatePassword(USERNAME, ENCODED_NEW_ONE);

    }

    @Test
    @DisplayName("비밀번호 변경 - 실패(틀린 기존 비밀번호 입력)")
    void changePasswordFailure() {

        NewPasswordRequest request = new NewPasswordRequest();
        request.setPassword(PASSWORD);
        request.setNewPassword(NEW_PASSWORD);

        // SecurityContext와 Authentication 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

        // db 안의 유저 비밀번호 조회시 -> dbPassword
        when(userRepository.findCurrnetPasswordByUsername(USERNAME)).thenReturn("dbPassword");

        String dbPassword = userRepository.findCurrnetPasswordByUsername(USERNAME);

        // 유저가 입력한 비밀번호와 저장되어있던 비밀번호 비교시 -> false(다르다)
        when(bCryptPasswordEncoder.matches(PASSWORD, dbPassword)).thenReturn(false);

        ResponseEntity<?> result = userService.changePassword(request);

        // body 확인, 업데이트 함수 사용했는지 확인
        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("비밀번호 변경 실패 했습니다.",body.getMessage());
        verify(userRepository, never()).updatePassword(anyString(), anyString());
    }


    @Test
    @DisplayName("프로필 가져오기 - 성공")
    void loadProfileSuccess(){

        // given
        Users user = Users.builder()
                .username(USERNAME)
                .firstName("test")
                .lastName("")
                .description("description here")
                .build();


        // SecurityContextHolder.getContext().getAuthentication().getName())
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findProfileByUsername(USERNAME)).thenReturn(Optional.ofNullable(user));

        // when
        ResponseEntity<?> result = userService.getProfile();

        // then
        String body = result.getBody().toString();

        // body 확인
        assertTrue(body.contains("firstName"));
        assertTrue(body.contains("lastName"));
        assertTrue(body.contains("description"));
        assertTrue(body.contains("message=프로필 정보 불러오기 성공 했습니다."));
        assertTrue(body.contains("status=200"));

        verify(userRepository, times(1)).findProfileByUsername(USERNAME);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("프로필 저장하기 - 성공")
    void saveProfiles() {
        // given
        UsersProfileRequest usersProfileRequest = new UsersProfileRequest();
        usersProfileRequest.setFirstName("first");
        usersProfileRequest.setLastName("last");
        usersProfileRequest.setDescription("test description");

        String access = "Bearer access";
        when(request.getHeader("Authorization")).thenReturn(access);
        when(jwtUtil.getUsername("access")).thenReturn(USERNAME);
        when(userRepository.isUsernameNotExist(USERNAME)).thenReturn(false);

        // when
        ResponseEntity<?> result = userService.setProfile(usersProfileRequest);

        // then
        verify(userRepository, times(1)).isUsernameNotExist(USERNAME);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertEquals(200, errorResponse.getStatus());
        assertEquals("유저 프로필 정보 업데이트 했습니다.", errorResponse.getMessage());

    }

    @Test
    @DisplayName("계정 정보 가져오기 - 성공")
    void getAccountSuccess() {
        // given
        Users user = Users.builder()
                .username(USERNAME)
                .birthday(LocalDate.of(1995,4,28))
                .gender("male")
                .spokenLanguage("Korean")
                .build();

        // SecurityContextHolder.getContext().getAuthentication().getName())
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findAccountByUsername(USERNAME)).thenReturn(Optional.ofNullable(user));

        // when
        ResponseEntity<?> result = userService.getAccount();

        // then
        String body = result.getBody().toString();

        // body 확인
        assertTrue(body.contains("username=test@test.com"));
        assertTrue(body.contains("birthday=1995-04-28"));
        assertTrue(body.contains("gender=male"));
        assertTrue(body.contains("message=계정 정보 불러오기 성공 했습니다."));
        assertTrue(body.contains("status=200"));


        verify(userRepository, times(1)).findAccountByUsername(USERNAME);
        assertEquals(HttpStatus.OK, result.getStatusCode());

    }

}

