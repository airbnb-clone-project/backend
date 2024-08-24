package com.airbnb_clone.chatting.repository;

import com.airbnb_clone.chatting.domain.Message;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    MongoTemplate mt;

    MessageRepository messageRepository;

    @BeforeEach
    void beforeEach() {
        messageRepository = new MessageRepository(mt);
        mt.dropCollection(Message.class);

    }

    @Test
    @DisplayName("메시지 저장 및 조회")
    void save_message_and_find() {
        ObjectId savedId = messageRepository.save(Message.of(null, 1, "content"));

        Message message = messageRepository.findById(savedId.toString()).get();

        assertThat(message.getNo()).isEqualTo(savedId);
        assertThat(message.getSenderNo()).isEqualTo(1);
        assertThat(message.getContent()).isEqualTo("content");
    }
}