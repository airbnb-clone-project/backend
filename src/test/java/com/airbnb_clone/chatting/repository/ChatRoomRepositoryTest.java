package com.airbnb_clone.chatting.repository;

import com.airbnb_clone.chatting.domain.ChatRoom;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
class ChatRoomRepositoryTest {

    @Autowired
    MongoTemplate mt;

    ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void beforeEach() {
        chatRoomRepository = new ChatRoomRepository(mt);
        mt.dropCollection(ChatRoom.class);
    }

    @Test
    @DisplayName("채팅방 저장 및 조회")
    void save_user_and_find() {
        ChatRoom chatRoom = ChatRoom.of(new ArrayList<>(List.of(0, 1)));

        ObjectId savedId = chatRoomRepository.save(chatRoom);

        ChatRoom findChatRoom = chatRoomRepository.findById(savedId).get();

        assertThat(findChatRoom).usingRecursiveComparison().isEqualTo(chatRoom);
    }

    @Test
    @DisplayName("기존 채팅방이 있는 지 확인")
    void check_existing_chat_room() {
        ChatRoom chatRoom = ChatRoom.of(new ArrayList<>(List.of(5, 10)));
        chatRoomRepository.save(chatRoom);

        ChatRoom result = chatRoomRepository.checkExistingChatRoom(5, 10).get();

        assertThat(result).usingRecursiveComparison().isEqualTo(chatRoom);
    }
}