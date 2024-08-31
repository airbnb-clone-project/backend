package com.airbnb_clone.chatting.domain;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Document(collection = "CHAT_ROOM")
@Getter
public class ChatRoom {

    @Id
    private ObjectId no;
    private List<Integer> participants;

    private LocalDateTime createAt;

    @Builder(access = PRIVATE)
    private ChatRoom(List<Integer> participants, LocalDateTime createAt) {
        this.participants = participants;
        this.createAt = createAt;
    }

    public static ChatRoom of(List<Integer> participants) {
        return ChatRoom.builder()
                .participants(participants)
                .createAt(LocalDateTime.now())
                .build();
    }
}