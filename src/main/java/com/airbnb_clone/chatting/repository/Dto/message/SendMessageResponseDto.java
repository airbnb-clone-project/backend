package com.airbnb_clone.chatting.repository.Dto.message;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

import static lombok.AccessLevel.*;

@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
@Getter
@ToString
public class SendMessageResponseDto {

    private String chatRoomId;
    private Integer senderNo;
    private String content;
    private LocalDateTime createAt;

    public static SendMessageResponseDto of(String chatRoomId, Integer senderNo, String content, LocalDateTime createAt) {
        return SendMessageResponseDto.builder()
                .chatRoomId(chatRoomId)
                .senderNo(senderNo)
                .content(content)
                .createAt(createAt)
                .build();
    }
}
