package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.ChatRoomRepository;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.UserRoomsResponseDto;
import com.airbnb_clone.config.CustomContextInitializer;
import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CustomContextInitializer.class})
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ChatRoomServiceTest {

    @Autowired
    MongoTemplate mt;

    @SpyBean
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatRoomService chatRoomService;

    @BeforeEach
    void beforeEach() {
        mt.dropCollection(ChatRoom.class);
    }

    @Test
    @DisplayName("채팅방 저장")
    void save_chat_room_and_find() {
        ChatRoom chatRoom = ChatRoom.of(new ArrayList<>(List.of(0, 1)));
        ObjectId objectId = new ObjectId(new Date());

        doReturn(objectId.toString()).when(chatRoomRepository).save(any(ChatRoom.class));

        ChatRoomNewResDto chatRoomNewResDto = chatRoomService.save(new ChatRoomNewReqDto(chatRoom.getParticipants()));

        assertThat(chatRoomNewResDto.getChatRoomId()).isEqualTo(objectId.toString());
    }

    @Test
    @DisplayName("채팅방 조회")
    void chat_room_find() {
        String savedId = chatRoomRepository.save(ChatRoom.of(List.of(0, 1)));

        ChatRoom chatRoom = chatRoomService.findById(savedId);

        assertThat(chatRoom.getNo().toString()).isEqualTo(savedId);
        assertThat(chatRoom.getParticipants()).usingRecursiveFieldByFieldElementComparator().containsExactly(0, 1);
    }

    @Test
    @DisplayName("채팅방 중복 저장 예외")
    void save_duplicate_chat_room_exception() {
        ChatRoom chatRoom = ChatRoom.of(new ArrayList<>(List.of(0, 1)));

        when(chatRoomRepository.checkExistingChatRoom(anyInt(), anyInt())).thenThrow(new DuplicateChatRoomException());

        assertThatThrownBy(() -> chatRoomRepository.checkExistingChatRoom(0, 1))
                .isInstanceOf(DuplicateChatRoomException.class)
                .hasMessage(ErrorCode.DUPLICATE_CHAT_ROOM.getMessage());
    }

    @Test
    @DisplayName("유저가 참여중인 모든 채팅방 조회")
    void user_join_chat_room_search() {
        chatRoomRepository.save(ChatRoom.of(List.of(0, 1)));
        chatRoomRepository.save(ChatRoom.of(List.of(0, 2)));

        List<UserRoomsResponseDto> userRooms = chatRoomService.findUserRooms(0);

        assertThat(userRooms.size()).isEqualTo(2);
        assertThat(userRooms.get(0).getReceiverId()).isEqualTo(1);
        assertThat(userRooms.get(1).getReceiverId()).isEqualTo(2);
    }

}
