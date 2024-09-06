package com.airbnb_clone.auth.service;

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
import static org.mockito.Mockito.when;

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



    // 토큰 발급 경우 refresh token 을
    //
    @Test
    @DisplayName("토큰 발급 성공")
    void reissueTokenSuccess() {

        // 요청으로 받는 refresh token 생성
        Cookie givenCookie[] = new Cookie[]{
                new Cookie(refresh, oldToken)
        };

        // 리프레시 토큰이 있는지 확인
        when(request.getCookies()).thenReturn(givenCookie);
        when(jwtUtil.isExpired(oldToken)).thenReturn(false);
        when(jwtUtil.getTokenType(oldToken)).thenReturn(refresh);
        when(refreshTokenRepository.existsByRefresh(oldToken)).thenReturn(false);
        when(jwtUtil.getUsername(oldToken)).thenReturn(username);

        ResponseEntity<?> result = reissueService.reissue(request, response);


        assertEquals(HttpStatus.OK, result.getStatusCode());


    }


}