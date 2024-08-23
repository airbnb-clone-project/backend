package com.airbnb_clone.auth.jwt;

import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import jakarta.servlet.*;

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

public class CustomLogoutFilter extends GenericFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public CustomLogoutFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;


        setFilterProcessesUrl("/api/auth/login"); // 엔드포인트 변경
    }

    @Override

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //
    }
}
