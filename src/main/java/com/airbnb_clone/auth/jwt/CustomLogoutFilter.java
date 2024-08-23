package com.airbnb_clone.auth.jwt;

import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * packageName    : com.airbnb_clone.auth.jwt
 * fileName       : CustomLogoutFilter
 * author         : doungukkim
 * date           : 2024. 8. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 23.        doungukkim       최초 생성
 */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 로그아웃 요청시 로그아웃이면 이 필터에 걸린다.
        String requestUri = request.getRequestURI();

        // 로그아웃이 아닐 경우 다음 필터로 넘어간다.
        if (!requestUri.matches("^\\/api/auth/logout$")) {

            filterChain.doFilter(request,response);
            return;
        }

        // get refresh token

        // check existence of refresh

        // check token expiration

        // 받은 토큰이 refresh 인지 확인

        // DB의 토큰과 비교

        /*
            로그아웃 진행
            refresh 토큰 DB 에서 제거
         */

        // refresh token 값 0

    }
}