package com.airbnb_clone.auth.dto.oauth2;

import com.airbnb_clone.auth.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.airbnb_clone.auth.dto.oauth2
 * fileName       : CustomOAuth2User
 * author         : doungukkim
 * date           : 2024. 8. 24.
 * description    : 유저 정보를 가져 온다 name, username, providerId
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 24.        doungukkim       최초 생성
 */
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final SocialUserDto socialUserDto;


    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return socialUserDto.getName();
    }

    public String getUsername() {
        return socialUserDto.getUsername();
    }

    public String getProviderId() {
        return socialUserDto.getProviderId();
    }
}
