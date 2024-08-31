package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.repository.Dto.message.MessagesResponseDto;
import com.airbnb_clone.chatting.service.MessageService;
import com.airbnb_clone.common.global.response.ApiResponse;
import com.airbnb_clone.exception.handler.CustomExceptionHandler;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    MessageService messageService;

    @InjectMocks
    MessageController messageController;

    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("참여중인 채팅방 메시지 조회")
    void join_chat_room_find_message() throws Exception {
        MessagesResponseDto message1 = MessagesResponseDto.of("chatRoomId", 0, "메시지 0", LocalDateTime.now());
        MessagesResponseDto message2 = MessagesResponseDto.of("chatRoomId", 1, "메시지 1", LocalDateTime.now());

        ApiResponse<List<MessagesResponseDto>> expectedResponse = ApiResponse.of("메시지 조회 성공!", 200, List.of(message1, message2));

        String expectedJson = new Gson().toJson(expectedResponse);

        when(messageService.list(anyString(), anyLong(), anyInt())).thenReturn(List.of(message1, message2));

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/chatRoomId?skip=0&limit=10"));
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        Type ApiResponseType = new TypeToken<ApiResponse<List<MessagesResponseDto>>>() {}.getType();

        ApiResponse<List<MessagesResponseDto>> actual = new Gson().fromJson(body, ApiResponseType);
        ApiResponse<List<MessagesResponseDto>> expected = new Gson().fromJson(expectedJson, ApiResponseType);

        log.info("actual = {}", actual);
        log.info("expected = {}", expected);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}