package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.RefreshToken;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.jwt.JwtUtil;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

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
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    String refresh = "refresh";
    String oldToken = "oldToken";
    String newToken = "newToken";
    String username = "test@test.com";


    @Test
    @DisplayName("토큰 발급 성공")
    void reissueTokenSuccess() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(refresh, oldToken)
        };

        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(oldToken)).thenReturn(false);
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(oldToken)).thenReturn(refresh);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(oldToken)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(oldToken)).thenReturn(username);

        ResponseEntity<?> result = reissueService.reissue(request, response);


        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(refreshTokenRepository, times(1)).deleteRefreshToken(oldToken);
        verify(refreshTokenRepository, times(1)).existsByRefresh(oldToken);
        verify(refreshTokenRepository, times(1)).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(body.getStatus(), 200);
        assertEquals(body.getMessage(), "토큰이 재발급 되었습니다.");
    }

    @Test
    @DisplayName("토큰 발급 실패 - 토큰 만료")
    void reissueTokenFail() {

        // 요청으로 받는 refresh token 생성
        Cookie[] givenCookie = new Cookie[]{
                new Cookie(refresh, oldToken)
        };


        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        // expiration check
        when(jwtUtil.isExpired(oldToken)).thenReturn(true); // 만료 상태
        // refresh token 이 맞는지 확인
        when(jwtUtil.getTokenType(oldToken)).thenReturn(refresh);
        // db 조회 token 대조
        when(refreshTokenRepository.existsByRefresh(oldToken)).thenReturn(false);
        // username 변수
        when(jwtUtil.getUsername(oldToken)).thenReturn(username);

        ResponseEntity<?> result = reissueService.reissue(request, response);

        assertEquals(HttpStatus.valueOf(401), result.getStatusCode());
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(refreshTokenRepository, never()).existsByRefresh(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(any());

        ErrorResponse body = (ErrorResponse) result.getBody();
        assertNotNull(body);
        assertEquals(body.getStatus(), 401);
        assertEquals(body.getMessage(), "리프레시 토큰이 만료되었습니다.");
    }
}