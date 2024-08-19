package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.ChatRoomRepository;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional(readOnly = false)
    public ChatRoomNewResDto save(ChatRoomNewReqDto chatRoomNewRequestDto) {
        ChatRoom chatRoom = ChatRoom.of(chatRoomNewRequestDto.getParticipants());

        ChatRoom chatRoom = ChatRoom.createChatRoom(chatRoomNewRequestDto.getParticipants());
        String savedId = chatRoomRepository.save(chatRoom).toString();

        return new ChatRoomNewResDto(savedId);
    }
}
