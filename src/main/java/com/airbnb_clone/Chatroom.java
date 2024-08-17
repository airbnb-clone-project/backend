package com.airbnb_clone;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(value = "CHATROOM")
public class Chatroom {
    @Id
    public ObjectId id;
    
    @Field(value = "CREATED_AT")
    public LocalDateTime createdAt;
    
    @Field(value = "PATICIPANTS")
    public List<Long> participants;
}