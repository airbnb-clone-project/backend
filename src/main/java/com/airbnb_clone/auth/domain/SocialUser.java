package com.airbnb_clone.auth.domain;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SocialUser extends BaseTime {
    private Long no;
    private Long userNo;
    private String provider;
    private String providerId;

    private SocialUser(LocalDateTime createdAt, LocalDateTime updatedAt, Long no,
                      Long userNo, String provider, String providerId) {
        super(createdAt, updatedAt);
        this.no = no;
        this.userNo = userNo;
        this.provider = provider;
        this.providerId = providerId;
    }
}