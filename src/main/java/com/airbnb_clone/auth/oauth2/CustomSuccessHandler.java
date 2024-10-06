package com.airbnb_clone.auth.oauth2;

import com.airbnb_clone.auth.domain.RefreshToken;
import com.airbnb_clone.auth.dto.oauth2.CustomOAuth2User;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.jwt.TokenUtil;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * packageName    : com.airbnb_clone.auth.oauth2
 * fileName       : CustomSuccessHandler
 * author         : doungukkim
 * date           : 2024. 8. 25.
 * description    : social login 응답, 리디렉션 주소 지정
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 25.        doungukkim       최초 생성
 */
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenUtil tokenUtil;


    @Override

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = ((CustomOAuth2User) authentication.getPrincipal());

        if (customUserDetails.getProviderId().equals("notAuthorized")) {
            response.sendRedirect("http://localhost:3000/login-failed"); // 로그인 실패 리디렉션 주소
            return;
        }

        String username = customUserDetails.getUsername();
        Long userNo = customUserDetails.getUserNo();

        // 두가지의 토큰 생성 -> 생성에 3개의 값(토큰, 이메일, 토큰 만료 길이)이 필요
//        String access = jwtUtil.createJwt("Authorization", username, 600000L); // 10분
        String refresh = jwtUtil.createJwt("refresh", username, userNo, 86400000L); // 24시간

        // refresh 토큰 저장
        Optional<String> token = refreshTokenRepository.findRefreshTokenByUsername(username);

        if (token.isPresent()) {
            String oldToken = token.get();
            refreshTokenRepository.deleteRefreshToken(oldToken);
        }
//        saveRefresh(username, refresh, 84600000L);

        RefreshToken refreshToken = new RefreshToken(username, refresh, 84600000L);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        // 응답 설정 4v2
        tokenUtil.addRefreshInCookie(response, refresh);
        response.sendRedirect("http://localhost:3000/"); // 리디렉션 주소

    }

    // refresh token을 DB에 저장 8v2
//    private void saveRefresh(String username, String refresh, Long expiredMs) {
//
//
//        // 24시간의 유효기간
//        LocalDateTime expirationDate = LocalDateTime.ofInstant(
//                Instant.ofEpochMilli(System.currentTimeMillis() + expiredMs),
//                ZoneId.systemDefault()
//        );
//
//        RefreshToken refreshTokenEntity = RefreshToken.builder()
//                .username(username)
//                .expiration(expirationDate)
//                .refreshToken(refresh)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        refreshTokenRepository.saveRefreshToken(refreshTokenEntity);
//    }
}
