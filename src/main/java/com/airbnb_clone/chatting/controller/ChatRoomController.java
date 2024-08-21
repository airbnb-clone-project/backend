package com.airbnb_clone.chatting.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.service.ChatRoomService;
import com.airbnb_clone.common.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping(produces = "application/json; charset=utf8")
    public HttpEntity<ApiResponse<ChatRoomNewResDto>> create(@RequestBody ChatRoomNewReqDto chatRoomNewReqDto) {
        ChatRoomNewResDto save = chatRoomService.save(chatRoomNewReqDto);

        return ResponseEntity.ok(
                ApiResponse.of("생성 성공!", 200, save)
        );
    }

}