package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.ChatRoomRepository;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomNewResDto save(ChatRoomNewReqDto chatRoomNewRequestDto) {
        duplicateChatRoomVerification(chatRoomNewRequestDto.getParticipants());

        ChatRoom chatRoom = ChatRoom.createChatRoom(chatRoomNewRequestDto.getParticipants());
        String savedId = chatRoomRepository.save(chatRoom).toString();

        return new ChatRoomNewResDto(savedId);
    }


    /**
     *
     * 채팅방이 존재하면 예외가 발생합니다.
     *
     * @param participants
     */
    private void duplicateChatRoomVerification(List<Integer> participants) {
        chatRoomRepository.checkExistingChatRoom(participants.get(0), participants.get(1)).ifPresent((chatRoom) -> {
            throw new DuplicateChatRoomException();
        });
    }
}
