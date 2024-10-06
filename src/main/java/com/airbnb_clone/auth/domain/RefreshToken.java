package com.airbnb_clone.auth.domain;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    private RefreshToken(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String username, String refreshToken, LocalDateTime expiration) {
        super(createdAt, updatedAt);
        this.no = no;
        this.username = username;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public RefreshToken (String username, String refresh, Long expiredMs) {
        super(LocalDateTime.now(), LocalDateTime.now());
        LocalDateTime expiration = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + expiredMs),
                ZoneId.systemDefault()
        );
        this.username = username;
        this.refreshToken = refresh;
        this.expiration = expiration;
    }
}