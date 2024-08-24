package com.airbnb_clone.chatting.repository.Dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SendMessageRequestDto {

    private String chatRoomId;
    private Integer senderNo;
    private String content;
}
