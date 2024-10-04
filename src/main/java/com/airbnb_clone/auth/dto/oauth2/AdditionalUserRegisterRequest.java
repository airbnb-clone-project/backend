package com.airbnb_clone.auth.dto.oauth2;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * packageName    : com.airbnb_clone.auth.dto.oauth2
 * fileName       : MoreUserRegisterRequest
 * author         : doungukkim
 * date           : 2024. 8. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 24.        doungukkim       최초 생성
 */
@Getter @Setter
public class AdditionalUserRegisterRequest {
    private String username;
    private LocalDate birthday;
    private String gender;
    private String spokenLanguage;
    private String country;
}