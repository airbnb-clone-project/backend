package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.ChatRoomRepository;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;

    @InjectMocks
    ChatRoomService chatRoomService;

    @Test
    @DisplayName("채팅방 저장")
    void save_chat_room_and_find() {
        ChatRoom chatRoom = ChatRoom.createChatRoom(new ArrayList<>(List.of(0, 1)));
        ObjectId objectId = new ObjectId(new Date());

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(objectId);

        ChatRoomNewResDto chatRoomNewResDto = chatRoomService.save(new ChatRoomNewReqDto(chatRoom.getParticipants()));

        assertThat(chatRoomNewResDto.getChatRoomId()).isEqualTo(objectId.toString());
    }

    @Test
    @DisplayName("채팅방 중복 저장 예외")
    void save_duplicate_chat_room_exception() {
        ChatRoom chatRoom = ChatRoom.createChatRoom(new ArrayList<>(List.of(0, 1)));

        when(chatRoomRepository.checkExistingChatRoom(anyInt(), anyInt())).thenThrow(new DuplicateChatRoomException());

        assertThatThrownBy(() -> chatRoomRepository.checkExistingChatRoom(0, 1))
                .isInstanceOf(DuplicateChatRoomException.class)
                .hasMessage(ErrorCode.DUPLICATE_CHAT_ROOM.getMessage());
    }

}