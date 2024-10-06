package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.RefreshToken;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.auth.jwt.TokenUtil;
import com.airbnb_clone.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TokenUtil tokenUtil;

    @Transactional
    public ResponseEntity<?> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 refresh token 을 가져온다.
        String givenToken = null;

        // 리프레시 토큰이 있는지 확인 하는 코드
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 이 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 쿠키가 있으니 refresh 가 있을경우 givenToken 에 추가
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
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 의 값이 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            expired check
            status : 401 , message : 리프레시 토큰이 만료되었습니다.
         */
        if (jwtUtil.isExpired(givenToken)) {
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
            ErrorResponse errorResponse = new ErrorResponse(401, "refresh token 이 아닙니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        /*
            DB를 조회해 refresh token 대조. 없으면 메시지
            status : 401 , message : 인증이 불가능한 토큰 입니다.
         */
        Boolean isNotExist = refreshTokenRepository.existsByRefresh(givenToken);
        if (isNotExist) {

            // response status code
            ErrorResponse errorResponse = new ErrorResponse(401, "인증이 불가능한 토큰 입니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 토큰에 문제가 없을 때 실행
        String username = jwtUtil.getUsername(givenToken);
        Long userNo = jwtUtil.getUserNoFromRefreshToken(givenToken);

        // 새로운 토큰 발급
        String newAccess = jwtUtil.createJwt("Authorization", username, userNo, 600000L); // 10분
        String newRefresh = jwtUtil.createJwt("refresh", username, userNo, 86400000L);

        // 기존 refresh token 삭제
        refreshTokenRepository.deleteRefreshToken(givenToken);

        // 새로운 refresh token DB 에 저장
        addRefreshToken(username,newRefresh,86400000L);
        /*
            response 생성
            status : 200 , message : 토큰이 재발급 되었습니다.
         */
//        response.setHeader("Authorization", newAccess);
        tokenUtil.addAccessInHeader(response, newAccess);
        tokenUtil.addRefreshInCookie(response, newRefresh);

        ErrorResponse errorResponse = new ErrorResponse(200, "토큰이 재발급 되었습니다.");
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    @Transactional
    public void addRefreshToken(String username, String refresh, Long expiredMs) {
        RefreshToken refreshToken = new RefreshToken(username, refresh, expiredMs);
        refreshTokenRepository.saveRefreshToken(refreshToken);
    }

}
