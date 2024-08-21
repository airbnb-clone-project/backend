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

    @Test
    @DisplayName("유저가 참여중인 모든 채팅방 조회")
    void user_join_chat_room_search() {
        ChatRoom chatRoom1 = ChatRoom.of(new ArrayList<>(List.of(1, 0)));
        ChatRoom chatRoom2 = ChatRoom.of(new ArrayList<>(List.of(1, 2)));
        ChatRoom chatRoom3 = ChatRoom.of(new ArrayList<>(List.of(2, 3)));
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        List<ChatRoom> userId_0 = chatRoomRepository.findUserRooms(0);
        List<ChatRoom> userId_1 = chatRoomRepository.findUserRooms(1);
        List<ChatRoom> userId_2 = chatRoomRepository.findUserRooms(2);
        List<ChatRoom> userId_3 = chatRoomRepository.findUserRooms(3);

        assertThat(userId_0).usingRecursiveFieldByFieldElementComparator().containsExactly(chatRoom1);
        assertThat(userId_1).usingRecursiveFieldByFieldElementComparator().containsExactly(chatRoom1, chatRoom2);
        assertThat(userId_2).usingRecursiveFieldByFieldElementComparator().containsExactly(chatRoom2, chatRoom3);
        assertThat(userId_3).usingRecursiveFieldByFieldElementComparator().containsExactly(chatRoom3);
    }
}