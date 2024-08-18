package com.airbnb_clone.chatting.repository.Dto.chatRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@AllArgsConstructor
@ToString
@Getter @Setter
public class ChatRoomNewResDto {

    private String chatRoomId;
}
