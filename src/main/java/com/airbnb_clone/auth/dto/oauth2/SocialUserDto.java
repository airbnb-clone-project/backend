package com.airbnb_clone.auth.dto.oauth2;

import lombok.Getter;
import lombok.Setter;

/**
 * packageName    : com.airbnb_clone.auth.dto.oauth2
 * fileName       : SocialUserDto
 * author         : doungukkim
 * date           : 2024. 8. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 24.        doungukkim       최초 생성
 */
@Setter
@Getter
public class SocialUserDto {

    String provider;
    String providerId;
    String username;
    String name;
}
