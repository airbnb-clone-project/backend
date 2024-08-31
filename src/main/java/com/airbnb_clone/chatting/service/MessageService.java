package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.domain.Message;
import com.airbnb_clone.chatting.repository.Dto.message.MessagesResponseDto;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageRequestDto;
import com.airbnb_clone.chatting.repository.MessageRepository;
import com.airbnb_clone.exception.chatting.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    public String save(SendMessageRequestDto sendMessageRequestDto) {
        ChatRoom chatRoom = chatRoomService.findById(sendMessageRequestDto.getChatRoomId());

        return messageRepository.save(Message.of(chatRoom, sendMessageRequestDto.getSenderNo(), sendMessageRequestDto.getContent()));
    }

    public List<MessagesResponseDto> list(String chatRoomId, Long skip, int limit) {
        return messageRepository.findMessages(chatRoomId, skip, limit).stream()
                .map(m -> MessagesResponseDto.of(
                        m.getChatRoom().getNo().toString(),
                        m.getSenderNo(),
                        m.getContent(),
                        m.getCreateAt()
                ))
                .toList();
    }

    public Message findById(String id) {
        return messageRepository.findById(id)
                .orElseThrow(MessageNotFoundException::new);
    }
}
