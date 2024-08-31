package com.airbnb_clone.chatting.repository.Dto.message;

import com.airbnb_clone.utills.LocalDateTimeUtils;
import lombok.*;

import java.time.LocalDateTime;

import static com.airbnb_clone.utills.LocalDateTimeUtils.format;
import static lombok.AccessLevel.*;

@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
@Getter
public class SendMessageResponseDto {

    private String chatRoomId;
    private Integer senderNo;
    private String content;
    private String createAt;

    public static SendMessageResponseDto of(String chatRoomId, Integer senderNo, String content, LocalDateTime createAt) {
        return SendMessageResponseDto.builder()
                .chatRoomId(chatRoomId)
                .senderNo(senderNo)
                .content(content)
                .createAt(format(createAt))
                .build();
    }

}
