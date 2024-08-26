package com.airbnb_clone.auth.dto.oauth2;

/**
 * packageName    : com.airbnb_clone.auth.dto.oauth2
 * fileName       : OAuth2Response
 * author         : doungukkim
 * date           : 2024. 8. 24.
 * description    : 소셜 로그인을 응답하기 위한 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 24.        doungukkim       최초 생성
 */
public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getUsername();

    String getName();
}
