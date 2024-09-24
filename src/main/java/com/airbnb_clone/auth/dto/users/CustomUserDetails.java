package com.airbnb_clone.auth.dto.users;

import com.airbnb_clone.auth.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * packageName    : com.airbnb_clone.auth.dto.user;
 * fileName       : CustomUsersDetails
 * author         : DK
 * date           : 24. 8. 22.
 * description    : UserDetails 는 Spring Security 가 이해할 수 있는 형식으로 사용자 정보를 제공
 *                  사용자 이름, 비밀번호, 권한 등 인증 및 권한 부여에 필요한 핵심 정보를 포함
 *                  UserDetails 를 구현할 때 실제 Users 객체의 구조에 따라 UserDetails 의 메서드들이 구현
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Users users;

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return users.getNo().toString();
            }
        });
        return collection;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}