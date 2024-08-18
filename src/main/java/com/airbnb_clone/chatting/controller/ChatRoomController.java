package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.common.global.response.ApiResDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping(produces = "application/json; charset=utf8")
    public HttpEntity<ApiResDto<ChatRoomNewResDto>> create(@RequestBody ChatRoomNewReqDto chatRoomNewReqDto) {
        ChatRoomNewResDto save = chatRoomService.save(chatRoomNewReqDto);

        return ResponseEntity.ok(
                ApiResDto.<ChatRoomNewResDto>builder()
                        .status(HttpStatus.OK.value())
                        .message("생성 성공!")
                        .data(save)
                        .build()
        );
    }
}
