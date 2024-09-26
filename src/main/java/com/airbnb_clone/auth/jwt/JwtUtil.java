package com.airbnb_clone.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;

/**
 * packageName    : com.airbnb_clone.auth.jwt;
 * fileName       : JwtUtil
 * author         : DK
 * date           : 24. 8. 22.
 * description    : 토큰 생성, 토큰 payload 정보 확인
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 * 24. 8. 29.        DK       access token 은 Bearer 추가되도록 수정
 */
@Component
public class JwtUtil {
    // 키 값 불러오기
    private final SecretKey secretKey;
    // 키 값 주입
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰 생성 (토큰 종류, 유저 이름, 유효 기간)
    public String createJwt(String tokenType, String username, Long userNo, Long expiredMs) {
        // access token
        if (tokenType.equals("Authorization")) {
            String jwt = Jwts.builder()
                    .claim("tokenType", tokenType) // 4v2
                    .claim("username", username)
                    .claim("userNo", userNo)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expiredMs))
                    .signWith(secretKey)
                    .compact();

            return "Bearer " + jwt;
        }

        // refresh token
        else{
            return Jwts.builder()
                    .claim("tokenType", tokenType)
                    .claim("username",username)
                    .claim("userNo", userNo)
                    .expiration(new Date(System.currentTimeMillis() + expiredMs))
                    .signWith(secretKey)
                    .compact();
        }
    }

    // 검증 : 토큰 종류 -> access, refresh 4v2
    public String getTokenType(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("tokenType", String.class);
    }

    // 검증 : 유저 이름
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 검증 : userNo
    public Long getUserNo(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userNo", Long.class);
    }

    // 검증 : 유효 기간
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

}
