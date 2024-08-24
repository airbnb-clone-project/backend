package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.domain.Message;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageRequestDto;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageResponseDto;
import com.airbnb_clone.chatting.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate smt;

    @MessageMapping("/send")
    public void sendMessage(@Payload SendMessageRequestDto sendMessageRequestDto) {
        String savedId = messageService.save(sendMessageRequestDto);
        Message message = messageService.findById(savedId);

        SendMessageResponseDto sendMessageResponseDto =
                SendMessageResponseDto.of(message.getChatRoom().getNo().toString(), message.getSenderNo(), message.getContent(), message.getCreateAt());

        smt.convertAndSend(String.format("/queue/rooms/%s", sendMessageResponseDto.getChatRoomId()), sendMessageResponseDto);
    }
}
