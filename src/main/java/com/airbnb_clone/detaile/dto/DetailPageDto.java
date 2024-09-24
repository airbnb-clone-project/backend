package com.airbnb_clone.detaile.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetailPageDto {

    private Long pinNo;
    private String imgUrl;
    private String title;
    private String description;
    private String link;
    private Long userNo;
    private String firstName;
    private String lastName;
    private String profileImgUrl;

    public DetailPageDto(long pinNo, String imgUrl, String title, String description, String link, long userNo, String firstName, String lastName, String profileImgUrl) {
        this.pinNo = pinNo;
        this.imgUrl = imgUrl;
        this.title = title;
        this.description = description;
        this.link = link;
        this.userNo = userNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImgUrl = profileImgUrl;
    }
}
