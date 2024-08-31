package com.airbnb_clone.chatting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static com.airbnb_clone.utills.LocalDateTimeUtils.now;
import static lombok.AccessLevel.*;

@Document(collection = "MESSAGE")
@Getter
@ToString
public class Message {

    @Id
    private ObjectId no;
    private ChatRoom chatRoom;
    private Integer senderNo;
    private String content;
    private LocalDateTime createAt;

    @Builder(access = PRIVATE)
    private Message(ChatRoom chatRoom, Integer senderNo, String content, LocalDateTime createAt) {
        this.chatRoom = chatRoom;
        this.senderNo = senderNo;
        this.content = content;
        this.createAt = createAt;
    }

    public static Message of(ChatRoom chatRoom, Integer senderNo, String content) {
        return Message.builder()
                .chatRoom(chatRoom)
                .senderNo(senderNo)
                .content(content)
                .createAt(now())
                .build();
    }
}
