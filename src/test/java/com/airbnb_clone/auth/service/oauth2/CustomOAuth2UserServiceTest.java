package com.airbnb_clone.auth.service.oauth2;

import com.airbnb_clone.auth.domain.SocialUser;
import com.airbnb_clone.auth.dto.oauth2.GoogleResponse;
import com.airbnb_clone.auth.dto.oauth2.OAuth2Response;
import com.airbnb_clone.auth.dto.oauth2.SocialUserDto;
import com.airbnb_clone.auth.repository.SocialUserRepository;
import com.airbnb_clone.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.airbnb_clone.auth.service.oauth2
 * fileName       : CustomOAuth2UserServiceTest
 * author         : doungukkim
 * date           : 2024. 9. 7.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 9. 7.        doungukkim       최초 생성
 */
class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private SocialUserRepository socialUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2UserRequest request;

    @Mock
    private OAuth2User oAuth2User;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    String sub = "providerId";
    String name = "tester";
    String email = "test@test.com";
    String provider = "google";

    @Test
    @DisplayName("OAuth2 service 성공")
    void loadSuccess() throws OAuth2AuthenticationException {

        // 가짜 OAuth2UserRequest 설정
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        when(request.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn(provider);

        // 가짜 OAuth2User 설정
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "sub", sub,
                "username", email,
                "name", name,
                "provider", provider
        ));
        // 가짜 SocialUser 설정
        SocialUser socialUser = SocialUser.builder()
                .providerId(sub)
                .provider(provider)
                .userNo(1L)
                .build();

        // super.loadUser() 메서드 mocking
        CustomOAuth2UserService customOAuth2UserServiceSpy = spy(customOAuth2UserService);
        doReturn(oAuth2User).when(customOAuth2UserServiceSpy).loadUser(request);



        // social user 에서 providerId 기준으로 userNo, provider, providerid 가져온다
        when(socialUserRepository.findByProviderId(sub)).thenReturn(Optional.of(socialUser));
        when(userRepository.isUsernameNotExist(email)).thenReturn(true);
        // 유저 username 기준 pk 조회
        when(userRepository.findNoByUsername(email)).thenReturn(1L);

        // when
        OAuth2User result = customOAuth2UserServiceSpy.loadUser(request);
//
//        verify(socialUserRepository, times(1)).findByProviderId(any());
//        verify(userRepository, times(1)).isUsernameNotExist(email);
//        verify(userRepository, times(1)).findNoByUsername(email);
//        verify(socialUserRepository, times(1)).registerSocialUser(socialUser);

        // 결과 확인
        assertNotNull(result);
        assertEquals(sub, result.getAttributes().get("sub"));
        assertEquals(email, result.getAttributes().get("username"));
        assertEquals(name, result.getAttributes().get("name"));
    }
}