package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.domain.Message;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageRequestDto;
import com.airbnb_clone.chatting.repository.MessageRepository;
import com.airbnb_clone.exception.chatting.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    public String save(SendMessageRequestDto sendMessageRequestDto) {
        ChatRoom chatRoom = chatRoomService.findById(sendMessageRequestDto.getChatRoomId());

        return messageRepository.save(Message.of(chatRoom, sendMessageRequestDto.getSenderNo(), sendMessageRequestDto.getContent()));
    }

    public Message findById(String id) {
        return messageRepository.findById(id)
                .orElseThrow(MessageNotFoundException::new);
    }
}
