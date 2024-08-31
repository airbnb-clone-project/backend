package com.airbnb_clone.chatting.repository;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.domain.Message;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

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
        mt.dropCollection(ChatRoom.class);
        mt.dropCollection(Message.class);

    }

    @Test
    @DisplayName("메시지 저장 및 조회")
    void save_message_and_find() {
        String savedId = messageRepository.save(Message.of(null, 1, "content"));

        Message message = messageRepository.findById(savedId).get();

        assertThat(message.getNo().toString()).isEqualTo(savedId);
        assertThat(message.getSenderNo()).isEqualTo(1);
        assertThat(message.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("참여중인 채팅방 메시지 조회")
    void join_chat_room_find_messages() {
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository(mt);
        String savedId = chatRoomRepository.save(ChatRoom.of(List.of(0, 1)));
        ChatRoom chatRoom = chatRoomRepository.findById(savedId).get();

        Message message3 = Message.of(chatRoom, 0, "메시지를 보냅니다. 3");

        String save = messageRepository.save(Message.of(chatRoom, 0, "메시지를 보냅니다. 1"));
        String save1 = messageRepository.save(Message.of(chatRoom, 1, "메시지를 보냅니다. 2"));

        Message message1 = messageRepository.findById(save).get();
        Message message2 = messageRepository.findById(save1).get();

        List<Message> messages = messageRepository.findMessages(chatRoom.getNo().toString(), 0L, 2);

        assertThat(messages).usingRecursiveFieldByFieldElementComparator().containsExactly(message1, message2);
    }
}