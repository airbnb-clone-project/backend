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
public class User extends BaseTime {
    private Long no;
    private String email;
    private boolean isSocial;
    private String refreshToken;
    private LocalDateTime tokenExpireAt;
    
    @Builder
    public User(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String email,
                boolean isSocial, String refreshToken, LocalDateTime tokenExpireAt) {
        super(createdAt, updatedAt);
        this.no = no;
        this.email = email;
        this.isSocial = isSocial;
        this.refreshToken = refreshToken;
        this.tokenExpireAt = tokenExpireAt;
    }
}