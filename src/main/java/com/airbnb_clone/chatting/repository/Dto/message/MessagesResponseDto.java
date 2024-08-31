package com.airbnb_clone.chatting.repository.Dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.airbnb_clone.utills.LocalDateTimeUtils.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@Getter
public class MessagesResponseDto {

    private String chatRoomId;
    private Integer userId;
    private String content;
    private String createAt;

    public static MessagesResponseDto of(String chatRoomId, Integer userId, String content, LocalDateTime createAt) {
        return MessagesResponseDto.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .content(content)
                .createAt(format(createAt))
                .build();
    }
}
