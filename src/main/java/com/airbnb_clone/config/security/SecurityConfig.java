package com.airbnb_clone.config.security;

//import com.airbnb_clone.jwt.JwtFilter;
//import com.airbnb_clone.jwt.JwtUtil;
//import com.airbnb_clone.jwt.LoginFilter;
//import com.airbnb_clone.repository.RefreshTokenRepository;
import com.airbnb_clone.auth.jwt.JwtFilter;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.jwt.LoginFilter;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * packageName    : com.airbnb_clone.config.security;
 * fileName       : SecurityConfig
 * author         : DK
 * date           : 24. 8. 22.
 * description    : Security 관련 @Bean 추가
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager 가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    // JwtUtil: jwt 를 다루게 될 클래스
    private final JwtUtil jwtUtil;
    // RefreshTokenRepository : refresh token 을 저장할 클래스
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean // password encoder 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean // authenticationManager 등록
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean // 필터체인
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
         *  csrf disable
         *  CSRF: CSRF 공격은 사용자가 인증된 세션을 통해 악의적인 요청을 보내는 공격. Spring Security 는 기본적으로 CSRF 보호를 활성화합니다. -> disable
         *  실제 서비스 환경에서도 CSRF 는 disable 하는 경우가 많음
         */
        http
                .csrf((auth) -> auth.disable());

        /*
         *  Form 로그인 방식 disable
         8  토큰 기반 인증이나 OAuth2 등을 사용할 때 Form 로그인을 비활성화할 수 있다.
         */
        http
                .formLogin((auth) -> auth.disable());

        /*
         *  http basic 인증 방식 disable
         *  HTTP Basic 인증: HTTP Basic 인증은 사용자 이름과 비밀번호를 Base64로 인코딩하여 HTTP 헤더에 포함시키는 방식. 간단하지만 보안에 취약할 수 있습니다. -> disable
         */
        http
                .httpBasic((auth) -> auth.disable());

        /*
         *  controller 의 인가 작업을 위한 코드
         *  접근 권한 설정
         *  다른 파트 개발을 위해 모든 요청에 대해 권한 허용
         */
        http
                // 모든 경로 권한 허용
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/**", "/ws/**").permitAll()
                        .anyRequest().authenticated()
                );
                // 권한 허용에 관해 정리되면 추가
//                .authorizeHttpRequests((auth) -> auth
//                        // 이 경로는 권안 허용, reissue: access가 만료되어 로그인이 안되어있는 상태기 때문에 permitAll()
//                        .requestMatchers("/api/auth/login", "/", "/api/auth/register", "/api/auth/reissue").permitAll()
//                        .anyRequest().authenticated()); // 나머지 다른 요청에 대해서는 로그인 한 사람만 가능

        /*
         *  OAuth2 적용시 로그인에서 무한 루프가 일어날 경우 .addFilterBefore() -> .addFilterAfter()
         *  JWTFilter(),  JWTUtil 사용
         */
        http
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        /*
         *  필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
         *  LoginFilter(), JWTUtil
         */
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);


        /*
         *  refresh token entity 추가되면 추가
         *  logout filter 추가
         */
//        http
//                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // 세션 설정(stateless)
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
