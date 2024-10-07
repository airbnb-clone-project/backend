package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.jwt.TokenUtil;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.airbnb_clone.auth.service
 * fileName       : ReissueServiceTest
 * author         : doungukkim
 * date           : 2024. 9. 7.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 9. 7.        doungukkim       최초 생성
 */
class ReissueServiceTest {

    @InjectMocks
    private ReissueService reissueService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenUtil tokenUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    static final String REFRESH_TOKEN_NAME = "refresh";
    static final String ACCESS_TOKEN_NAME = "Authorization";
    static final String OLD_TOKEN = "oldToken";
    static final String NEW_TOKEN = "newToken";
    static final String USERNAME = "test@test.com";


    @Test
    @DisplayName("토큰 발급 성공")
    void reissueTokenSuccess() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(REFRESH_TOKEN_NAME, OLD_TOKEN)
        };

        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(OLD_TOKEN)).thenReturn(false);
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(OLD_TOKEN)).thenReturn(REFRESH_TOKEN_NAME);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(OLD_TOKEN)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(OLD_TOKEN)).thenReturn(USERNAME);
        when(jwtUtil.getUserNoFromRefreshToken(OLD_TOKEN)).thenReturn(1L);

        when(jwtUtil.createJwt(eq(ACCESS_TOKEN_NAME), anyString(), anyLong(), anyLong())).thenReturn(NEW_TOKEN);
        when(jwtUtil.createJwt(eq(REFRESH_TOKEN_NAME), anyString(), anyLong(), anyLong())).thenReturn(NEW_TOKEN);

        ResponseEntity<?> result = reissueService.reissueRefreshToken(request, response);


        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(refreshTokenRepository, times(1)).deleteRefreshToken(OLD_TOKEN);
        verify(refreshTokenRepository, times(1)).existsByRefresh(OLD_TOKEN);
        verify(refreshTokenRepository, times(1)).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(200,body.getStatus());
        assertEquals("토큰이 재발급 되었습니다.", body.getMessage());
    }

    @Test
    @DisplayName("토큰 발급 실패 - 토큰 없음")
    void reissueTokenFail2() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(REFRESH_TOKEN_NAME, OLD_TOKEN)
        };


        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(null); // 토큰 없음
        // expiration check
        when(jwtUtil.isExpired(OLD_TOKEN)).thenReturn(false);
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(OLD_TOKEN)).thenReturn(REFRESH_TOKEN_NAME);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(OLD_TOKEN)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(OLD_TOKEN)).thenReturn(USERNAME);

        ResponseEntity<?> result = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.valueOf(401), result.getStatusCode());
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(refreshTokenRepository, never()).existsByRefresh(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("refresh token 이 없습니다.", body.getMessage());
    }

    @Test
    @DisplayName("토큰 발급 실패 - 토큰 값이 없음")
    void reissueTokenFail3() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(REFRESH_TOKEN_NAME, null) // 토큰 값이 없음
        };


        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(OLD_TOKEN)).thenReturn(false);
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(OLD_TOKEN)).thenReturn(REFRESH_TOKEN_NAME);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(OLD_TOKEN)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(OLD_TOKEN)).thenReturn(USERNAME);

        ResponseEntity<?> result = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.valueOf(401), result.getStatusCode());
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(refreshTokenRepository, never()).existsByRefresh(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("refresh token 의 값이 없습니다.", body.getMessage());
    }


    @Test
    @DisplayName("토큰 발급 실패 - 토큰 만료")
    void reissueTokenFail() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(REFRESH_TOKEN_NAME, OLD_TOKEN)
        };


        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(OLD_TOKEN)).thenReturn(true); // 만료 상태
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(OLD_TOKEN)).thenReturn(REFRESH_TOKEN_NAME);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(OLD_TOKEN)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(OLD_TOKEN)).thenReturn(USERNAME);

        ResponseEntity<?> result = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.valueOf(401), result.getStatusCode());
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(refreshTokenRepository, never()).existsByRefresh(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("리프레시 토큰이 만료되었습니다.",body.getMessage());
    }

    @Test
    @DisplayName("토큰 발급 실패 - 받은 토큰이 refresh 가 아님")
    void reissueTokenFail4() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(REFRESH_TOKEN_NAME, OLD_TOKEN)
        };

        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(OLD_TOKEN)).thenReturn(false);
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(OLD_TOKEN)).thenReturn("Authorization"); // 리프래시 토큰 패이로드 확인결과 refresh token 이 아님
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(OLD_TOKEN)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(OLD_TOKEN)).thenReturn(USERNAME);

        ResponseEntity<?> result = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.valueOf(401), result.getStatusCode());
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(refreshTokenRepository, never()).existsByRefresh(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("refresh token 이 아닙니다.", body.getMessage());
    }

    @Test
    @DisplayName("토큰 발급 실패 - DB의 refresh 와 불일치")
    void reissueTokenFail5() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(REFRESH_TOKEN_NAME, OLD_TOKEN)
        };

        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(OLD_TOKEN)).thenReturn(false);
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(OLD_TOKEN)).thenReturn(REFRESH_TOKEN_NAME);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(OLD_TOKEN)).thenReturn(true); // Db 조회 결과 같은 토큰 없음
        // username 변수
        when(jwtUtil.getUsername(OLD_TOKEN)).thenReturn(USERNAME);

        ResponseEntity<?> result = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.valueOf(401), result.getStatusCode());
        verify(refreshTokenRepository, times(1)).existsByRefresh(OLD_TOKEN);
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(refreshTokenRepository, never()).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("인증이 불가능한 토큰 입니다.", body.getMessage());
    }


}