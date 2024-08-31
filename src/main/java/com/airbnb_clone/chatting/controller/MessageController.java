package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.domain.Message;
import com.airbnb_clone.chatting.repository.Dto.message.MessagesResponseDto;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageRequestDto;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageResponseDto;
import com.airbnb_clone.chatting.service.MessageService;
import com.airbnb_clone.common.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

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

    @GetMapping(value = "/api/messages/{chatRoomId}", produces = "application/json; charset=utf8")
    @ResponseBody
    public HttpEntity<ApiResponse<List<MessagesResponseDto>>> list(@PathVariable String chatRoomId, Long skip, int limit) {
        List<MessagesResponseDto> list = messageService.list(chatRoomId, skip, limit);

        return ResponseEntity.ok(
                ApiResponse.of("메시지 조회 성공!", OK.value(), list)
        );
    }
}
