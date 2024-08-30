package com.airbnb_clone.auth.service.oauth2;

import com.airbnb_clone.auth.domain.SocialUser;
import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.oauth2.CustomOAuth2User;
import com.airbnb_clone.auth.dto.oauth2.GoogleResponse;
import com.airbnb_clone.auth.dto.oauth2.OAuth2Response;
import com.airbnb_clone.auth.dto.oauth2.SocialUserDto;
import com.airbnb_clone.auth.repository.SocialUserRepository;
import com.airbnb_clone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * packageName    : com.airbnb_clone.auth.service.oauth2
 * fileName       : CustomOAuth2UserService
 * author         : doungukkim
 * date           : 2024. 8. 24.
 * description    : oauth2 user service, 저장, OAuth2User 객체 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 24.        doungukkim       최초 생성
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        // name, username, providerId
        OAuth2User oAuth2User = super.loadUser(request);

        // 레지스트레이션 아이디 : google
        String registration = request.getClientRegistration().getRegistrationId();

        // google의 providerId, name, username
        OAuth2Response oAuth2Response = null;

        if (registration.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // DB에 저장
        String providerId = oAuth2Response.getProviderId();
        String username = oAuth2Response.getUsername();
        String name = oAuth2Response.getName();

        // social user 에서 providerId 기준으로 userNo, provider, providerid 가져온다
        Optional<SocialUser> existData = socialUserRepository.findByProviderId(providerId);


        /*
            * 일반 로그인 기록이 있을경우 변화 없음
            * 한번도 소셜 로그인을 하지 않은 경우 새로 생성
            * 로그인 기록이 있을경우 이름만 업데이트
         */

        // username으로 db를 조회
        boolean userNotExist = userRepository.isUsernameNotExist(username);


        // user에 username이 있고(userNotExist) social user에 데이터가 없을 경우(existData.isEmpty()) 일반 로그인 유저

        // 예외처리

        if ((!userNotExist) && (existData.isEmpty())){

            SocialUserDto socialUserDto = new SocialUserDto();
            socialUserDto.setName("notAuthorized");
            socialUserDto.setUsername("notAuthorized");
            socialUserDto.setProviderId("notAuthorized");
            socialUserDto.setProvider("notAuthorized");


            return new CustomOAuth2User(socialUserDto);
        }

        if (existData.isEmpty()) {
            // 없을경우 Users, SocialUser, RefreshToken 추가
            // Users 객체 빌드
            Users user = Users.builder()
                    .username(username)
                    .firstName(name)
                    .lastName("")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isSocial(true)
                    .build();

            // user 저장
            userRepository.registerUser(user);


            // 유저 username 기준 primary key 조회
            Long userNo = userRepository.findNoByUsername(username);

            // social user 객체 빌드
            SocialUser socialUser = SocialUser.builder()
                    .userNo(userNo) // Users 의 id
                    .provider(oAuth2Response.getProvider())
                    .providerId(oAuth2Response.getProviderId())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // social user 저장
            socialUserRepository.registerSocialUser(socialUser);


            SocialUserDto socialUserDto = new SocialUserDto();
            socialUserDto.setName(name);
            socialUserDto.setProvider(oAuth2Response.getProvider());
            socialUserDto.setProviderId(providerId);
            socialUserDto.setUsername(username);


            return new CustomOAuth2User(socialUserDto);
        }
        /*
            이미 소셜 로그인을 해봤던 경우
            업데이트만 진행
         */

        else {
            /*
            google 계정의 이름이 바뀌었을 수 있으니 이름 다시 저장
            username 기준 firstName 업데이트
            */
            userRepository.updateUsername(username, name);

            SocialUserDto socialUserDto = new SocialUserDto();
            socialUserDto.setName(name);
            socialUserDto.setProvider(existData.get().getProvider());
            socialUserDto.setProviderId(existData.get().getProviderId());
            socialUserDto.setUsername(username);


            return new CustomOAuth2User(socialUserDto);
        }
    }
}
