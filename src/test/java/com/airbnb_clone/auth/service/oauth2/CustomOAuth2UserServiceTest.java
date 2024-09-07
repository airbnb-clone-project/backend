package com.airbnb_clone.auth.service.oauth2;

import com.airbnb_clone.auth.repository.SocialUserRepository;
import com.airbnb_clone.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("")
    void loadSuccess() {
        OAuth2AccessToken accessToken = Mockito.mock(OAuth2AccessToken.class);

    }
}