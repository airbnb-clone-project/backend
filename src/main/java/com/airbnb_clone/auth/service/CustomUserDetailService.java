package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.dto.users.CustomUserDetails;
import com.airbnb_clone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.airbnb_clone.auth.dto.service;
 * fileName       : CustomUsersDetailsService
 * author         : DK
 * date           : 24. 8. 22.
 * description    : DB 에서 유저 정보를 호출해 로그인 요청시 유저의 입력 값과 비교
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + "아이디를 찾을 수 없습니다."));
    }

}
