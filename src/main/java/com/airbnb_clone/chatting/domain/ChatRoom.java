package com.airbnb_clone.chatting.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;


@Document(collection = "CHAT_ROOM")
@Getter
public class ChatRoom {

    @Id
    private ObjectId no;
    private List<Integer> participants;

    @CreatedDate
    private Date createAt;

    @Builder
    private ChatRoom(List<Integer> participants) {
        this.participants = participants;
    }

    public static ChatRoom createChatRoom(List<Integer> participants) {
        return ChatRoom.builder()
                .participants(participants)
                .build();
    }
}
