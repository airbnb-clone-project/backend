package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.RefreshToken;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * packageName    : com.airbnb_clone.auth.service;
 * fileName       : RefreshTokenRepository
 * author         : DK
 * date           : 24. 8. 23.
 * description    : 토큰 reissue
 *                  리이슈 요청시 기존 토큰 삭제 후 새로운 토큰 저장
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 23.        DK       최초 생성
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReissueService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 refresh token 을 가져온다.
        String givenToken = null;

        // 리프레시 토큰이 있는지 확인 하는 코드
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")){
                givenToken = cookie.getValue();
            }
        }

        /*
            리프레시 토큰 없음
            status : 401 , message : refresh token 이 없습니다.
         */
        if (givenToken == null) {
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 이 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            expired check
            status : 401 , message : 리프레시 토큰이 만료되었습니다.
         */
        try {
            jwtUtil.isExpired(givenToken);
        } catch (ExpiredJwtException e) {

            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "리프레시 토큰이 만료되었습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            토큰이 refresh token 이 맞는지 확인
            status : 401 , message : refresh token이 아닙니다.
         */
        String tokenType = jwtUtil.getTokenType(givenToken);

        if (!tokenType.equals("refresh")) {

            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token이 아닙니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            DB를 조회해 refresh token 비교. 없으면 메시지
            status : 401 , message : 인증이 불가능한 토큰 입니다.
         */
        Boolean isExist = refreshTokenRepository.existsByRefresh(givenToken);
        if (isExist) {

            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "인증이 불가능한 토큰 입니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 토큰에 문제가 없을 때 실행
        String username = jwtUtil.getUsername(givenToken);

        // 새로운 토큰 발급
        String newAccess = jwtUtil.createJwt("Authorization", username, 600000L); // 10분
        String newRefresh = jwtUtil.createJwt("refresh", username, 86400000L);

        // 기존 refresh token 삭제
        refreshTokenRepository.deleteRefreshToken(givenToken);

        // 새로운 refresh token DB 에 저장
        addRefreshToken(jwtUtil.getUsername(givenToken),newRefresh,86400000L);

        /*
            response 생성
            status : 200 , message : 토큰이 재발급 되었습니다.
         */
        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        ErrorResponse errorResponse = new ErrorResponse(200, "토큰이 재발급 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    @Transactional
    public void addRefreshToken(String username, String refresh, Long expiredMs) {

        LocalDateTime expiration = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + expiredMs),
                ZoneId.systemDefault()
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .refreshToken(refresh)
                .expiration(expiration)
                .build();

        refreshTokenRepository.saveRefreshToken(refreshToken);
    }

    // 쿠키 생성
    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);  // https통신시 사용
        // cookeie.setPath("/");    // 쿠키가 적용될 범위
        cookie.setHttpOnly(true);

        return cookie;
    }

}
