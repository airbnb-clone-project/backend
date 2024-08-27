package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.repository.Dto.chatRoom.UserRoomsResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.service.ChatRoomService;
import com.airbnb_clone.common.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping(produces = "application/json; charset=utf8")
    public HttpEntity<ApiResponse<ChatRoomNewResDto>> create(@RequestBody ChatRoomNewReqDto chatRoomNewReqDto) {
        return ResponseEntity.ok(
                ApiResponse.of("생성 성공!", OK.value(), chatRoomService.save(chatRoomNewReqDto))
        );
    }

    @GetMapping("/{userId}")
    public HttpEntity<ApiResponse<List<UserRoomsResponseDto>>> findUserRooms(@PathVariable Integer userId) {
        return ResponseEntity.ok(
                ApiResponse.of("채팅방 조회 완료!", OK.value(), chatRoomService.findUserRooms(userId))
        );
    }

}