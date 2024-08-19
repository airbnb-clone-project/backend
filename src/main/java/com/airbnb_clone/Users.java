package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Users extends BaseTime {
    private Long no;
    private String username; // 이메일
    private String password;
    private String firstName; // 닉네임
    private String lastName; // 닉네임
    private String spokenLanguage;
    private String country;
    private LocalDate birthday;
    private String profileImgUrl; // 프로필 사진
    private boolean isSocial;

    private Users(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String username,
                  boolean isSocial, String firstName, String lastName, LocalDate birthday, String profileImgUrl, String country, String spokenLanguage) {
        super(createdAt, updatedAt);
        this.no = no;
        this.username = username;
        this.isSocial = isSocial;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.profileImgUrl = profileImgUrl;
        this.country = country;
        this.spokenLanguage = spokenLanguage;
    }
}