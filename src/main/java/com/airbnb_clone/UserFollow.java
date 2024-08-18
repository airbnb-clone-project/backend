package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserFollow extends BaseTime {
    private Long no;
    private Long followerNo;
    private Long followedNo;
    private boolean isActive;

    @Builder
    public UserFollow(LocalDateTime createdAt, LocalDateTime updatedAt, Long no,
                      Long followerNo, Long followedNo, boolean isActive) {
        super(createdAt, updatedAt);
        this.no = no;
        this.followerNo = followerNo;
        this.followedNo = followedNo;
        this.isActive = isActive;
    }
}