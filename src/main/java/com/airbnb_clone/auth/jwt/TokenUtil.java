package com.airbnb_clone.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.airbnb_clone.auth.jwt
 * fileName       : Token
 * author         : doungukkim
 * date           : 2024. 10. 6.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 10. 6.        doungukkim       최초 생성
 */
@Component
public class TokenUtil {

    private static final String ACCESS = "Authorization";
    private static final String REFRESH = "refresh";


    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 24시간
        // cookie.setSecure(true);  // https 통신시 사용
        cookie.setPath("/");    // 쿠키가 적용될 범위
        cookie.setHttpOnly(true);

        return cookie;
    }

    public void addRefreshInCookie(HttpServletResponse response, String RefreshToken) {
        Cookie refresh = createCookie(REFRESH, RefreshToken);
        response.addCookie(refresh);

    }

    public void addAccessInHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS, accessToken);
    }
}
