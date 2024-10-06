package com.airbnb_clone.auth.jwt;

import com.airbnb_clone.auth.domain.RefreshToken;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.dto.users.UserLoginRequest;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import com.airbnb_clone.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * packageName    : com.airbnb_clone.auth.jwt;
 * fileName       : LoginFilter
 * author         : DK
 * date           : 24. 8. 22.
 * description    : 로그인 필터 /api/auth/login 에 대한 POST 요청을 감시
 *                  요청 정보를 확인하고 acc/refresh token 발급
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, TokenUtil customToken) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenUtil = customToken;
        setFilterProcessesUrl("/api/auth/login"); // 엔드포인트 변경
    }

    /*
        필터로 http 정보를 받아 확인 후 가공
        로그인 시도: 들어온 정보를 바탕으로 authenticationManager에게 인증을 위한 토큰 생성후 반환
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 클라이언트 요청에서 username, password 가져온다
        UserLoginRequest loginRequest = new UserLoginRequest();

        // json 형식으로 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, UserLoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

         /*
            username 과 password 를 검증하기 위해 token 에 담아 AuthenticationManager 을 통해 검증
            AuthenticationManager 로 전달을 하기위해 UsernamePasswordAuthenticationToken 으로 캡슐화
         */
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        // token 에 담은 검증을 위한 AuthenticationManager 로 전달해서 인증 시도
        return authenticationManager.authenticate(authToken);
    }

    /*
        성공: AuthenticationManager 로 보내진 토큰을 바탕으로 인증 성공시 Authentication 객체 생성 이를 바탕으로 토큰 생성
        access/refresh 를 위한 코드 4v2
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 유저 정보
        String username = authResult.getName();

        // db 조회를 통해 userNo 불러오기
        Long userNo = userRepository.findNoByUsername(username);

        String token = refreshTokenRepository.findRefreshTokenByUsername(username).orElseThrow(null);
        refreshTokenRepository.deleteRefreshToken(token);

        // 두가지의 토큰 생성 -> 생성에 4개의 값(토큰, 이메일, 유저NO, 토큰 만료 길이)이 필요
        String access = jwtUtil.createJwt("Authorization", username, userNo, 600000L); // 10분
        String refresh = jwtUtil.createJwt("refresh", username, userNo, 86400000L); // 24시간

        // refresh 토큰 저장
        saveRefresh(username, refresh, 84600000L);

        // 응답 설정 4v2
        tokenUtil.addAccessInHeader(response, access);
        tokenUtil.addRefreshCookie(response, refresh);

        setBody(response,200,"일반 로그인이 완료 되었습니다.");
    }

    /*
        실패: 인증된 Authentication 객체가 생성되지 않는다.
        따라서 실패 이유를 담은 AuthenticationException이 발생.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // status : 401, message : 로그인 실패 했습니다.
        setBody(response,401,"로그인 실패 했습니다.");
    }


    // refresh token을 DB에 저장 8v2
    private void saveRefresh(String username, String refresh, Long expiredMs) {

        // 24시간의 유효기간
        LocalDateTime expirationDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + expiredMs),
                ZoneId.systemDefault()
        );

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .expiration(expirationDate)
                .refreshToken(refresh)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.saveRefreshToken(refreshTokenEntity);
    }

    // refresh token을 담기위한 쿠키 생성 메소드 4v2
//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(24 * 60 * 60); // 24시간
//        // cookie.setSecure(true);  // https 통신시 사용
//        cookie.setPath("/");    // 쿠키가 적용될 범위
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }

    public void setBody(HttpServletResponse response, int status, String message) throws IOException {

        response.setContentType("application/json");
        response.setStatus(status);

        // response body
        ErrorResponse errorResponse = new ErrorResponse(status, message);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
