package com.airbnb_clone.chatting.repository;

import com.airbnb_clone.chatting.domain.Message;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final MongoTemplate mt;

    public String save(Message message) {
        mt.insert(message);

        return message.getNo().toString();
    }

    public Optional<Message> findById(String id) {
        return Optional.ofNullable(mt.findById(id, Message.class));
    }

}
