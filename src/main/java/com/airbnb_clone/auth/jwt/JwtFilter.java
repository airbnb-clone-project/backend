package com.airbnb_clone.auth.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * packageName    : com.airbnb_clone.auth.jwt;
 * fileName       : JwtFilter
 * author         : DK
 * date           : 24. 8. 22.
 * description    : access jwt 유효성을 검사하는 클래스. client 한테 받은 토큰을 확인.
 * <p>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    /*
        OncePerRequestFilter 을 사용하기 위한 필수 함수
        토큰 관리
        access/refresh token 을 사용하면서 함수 내부 전부 바뀜 이전 코드는 주석 5v2
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // header 에서 access 가져옴

        /*
            토큰이 없으면 다음 필터로 넘어 간다.
            권한이 필요 없는 경우도 있기 때문에 다음 필터로 넘긴다. ex) /login, /reissue
         */

        /*
            토큰 만료 여부 확인. access token 만료 시간 확인
            만료시 예외 반환
         */

        /*
            토큰을 까서 Authorization 확인
            header 에 access 로 보낼 수 있지만 토큰이 access 토큰이 맞는지 확인
         */

        // token 이 가지고 있던 username 값을 획득

        // 토큰 생성에 필요한 값만 set

        /*
            Users 객체를 CustomUserDetails(UserDetails) 객체로 변환
            CustomUserDetails : spring security 에서 사용자 인증을 처리(이해)하기 위해 사용 되는 객체
         */

        /*
            Authentication 객체를 생성하여, 사용자의 인증 정보를 담는다.
            여기서는 UsernamePasswordAuthenticationToken을 사용하여 인증 토큰을 생성하고,
            이 토큰에는 사용자 정보(customUserDetails)와 권한(getAuthorities())이 포함됩니다.
         */

        /*
            이 부분이 중요한데, SecurityContextHolder를 통해 SecurityContext에 인증 정보를 저장.
            이로 인해 이후의 요청들에서 이 사용자는 인증된 사용자로 간주됩니다.
         */
    }

}
