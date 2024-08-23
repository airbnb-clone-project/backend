package com.airbnb_clone.auth.jwt;

import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

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

        // post 요청이 아닐 경우 다음 필터로 넘어간다.
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // check existence of refresh
        if (refresh == null) {

            // status : 401, message : refresh token 이 없습니다.
            setBody(response,401,"refresh token 이 없습니다.");
        }

        // check token expiration
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            // status : 401, message : refresh token 이 만료되었습니다.
            setBody(response, 401, "refresh token 이 만료되었습니다.");
        }

        // 받은 토큰이 refresh 인지 확인
        String tokenType = jwtUtil.getTokenType(refresh);
        if (!tokenType.equals("refresh")) {


            // status : 401, message : refresh token 이 만료되었습니다.
            setBody(response,401,"refresh token 이 만료되었습니다.");
        }

        // DB의 토큰과 비교
        Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
        if (isExist) {

            // status : 401, message : 인증 불가능한 토큰 입니다.
            setBody(response, 401, "인증 불가능한 토큰 입니다.");
        }

        /*
            로그아웃 진행
            refresh 토큰 DB 에서 제거
         */
        refreshTokenRepository.deleteRefreshToken(refresh);

        // refresh token 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        // status : 200, message : 로그아웃이 완료 되었습니다.
        setBody(response, 200, "로그아웃이 완료 되었습니다.");
    }

    public void setBody(HttpServletResponse response, int status, String message) throws IOException {

        response.setContentType("application/json");
        response.setStatus(status);

        // response body
        ErrorResponse errorResponse = new ErrorResponse(status, message);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}