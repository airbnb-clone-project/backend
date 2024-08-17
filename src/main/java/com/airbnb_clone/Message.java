package com.airbnb_clone;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(value = "MESSAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Message {
    @Id
    public ObjectId id;
    
    @Field(value = "SENDER_NO")
    public Object senderNo;
    
    @Field(value = "CONTENT")
    public String content;
    
    @Field(value = "CREATED_AT")
    public LocalDateTime createdAt;
}