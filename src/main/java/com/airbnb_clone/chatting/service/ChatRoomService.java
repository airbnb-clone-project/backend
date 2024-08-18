package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.ChatRoomRepository;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomNewResDto save(ChatRoomNewReqDto chatRoomNewRequestDto) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(chatRoomNewRequestDto.getParticipants());

        String savedId = chatRoomRepository.save(chatRoom).toString();

        return new ChatRoomNewResDto(savedId);
    }
}
