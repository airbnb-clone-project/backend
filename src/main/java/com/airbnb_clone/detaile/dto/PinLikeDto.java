package com.airbnb_clone.detaile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PinLikeDto {

    private Long pinLikeNo;
    private Long pinNo;
    private int emojiNo;
    private Long liker;
//    private String userNickname;
    private String firstname;
    private String lastname;
    private String userProfileImg;
    private Instant createdAt;
}

