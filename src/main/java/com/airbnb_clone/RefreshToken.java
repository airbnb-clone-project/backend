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
public class RefreshToken extends BaseTime {
    private Long no;
    private String username; // 이메일
    private String refreshToken;
    private LocalDateTime expiration;

    @Builder
    public RefreshToken(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String username, String refreshToken, LocalDateTime expiration) {
        super(createdAt, updatedAt);
        this.no = no;
        this.username = username;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}

