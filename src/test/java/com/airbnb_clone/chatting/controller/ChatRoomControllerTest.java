package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.common.global.response.ApiResponse;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.service.ChatRoomService;
import com.airbnb_clone.exception.handler.CustomExceptionHandler;
import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.ErrorResponse;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.airbnb_clone.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomControllerTest {

    @Mock
    ChatRoomService chatRoomService;

    @InjectMocks
    ChatRoomController chatRoomController;

    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatRoomController)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("채팅방 저장")
    void chat_room_save_and_find() throws Exception {
        ObjectId objectId = new ObjectId(new Date());
        ChatRoomNewReqDto chatRoomNewReqDto = new ChatRoomNewReqDto(new ArrayList<>(List.of(0, 1)));
        ChatRoomNewResDto chatRoomNewResDto = new ChatRoomNewResDto(objectId.toString());

        ApiResponse<ChatRoomNewResDto> resApi = ApiResponse.of("생성 성공!", 200, chatRoomNewResDto);
        String expectedJson = new Gson().toJson(resApi);

        doReturn(chatRoomNewResDto).when(chatRoomService).save(any(ChatRoomNewReqDto.class));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/chat-room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(chatRoomNewReqDto)));

        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        ApiResponse actual = new Gson().fromJson(body, ApiResponse.class);
        ApiResponse expected = new Gson().fromJson(expectedJson, ApiResponse.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("채팅방 중복 저장 예외")
    void chat_room_duplicate_save_exception() throws Exception {
        ChatRoomNewReqDto chatRoomNewReqDto = new ChatRoomNewReqDto(new ArrayList<>(List.of(0, 1)));

        when(chatRoomService.save(any(ChatRoomNewReqDto.class))).thenThrow(new DuplicateChatRoomException());

        ApiResponse<ErrorResponse> expectedResponse =
                ApiResponse.of(DUPLICATE_CHAT_ROOM.getMessage(), DUPLICATE_CHAT_ROOM.getStatus(), ErrorResponse.of(DUPLICATE_CHAT_ROOM));
        String expectedJson = new Gson().toJson(expectedResponse);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/chat-room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(chatRoomNewReqDto)));

        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        ErrorResponse actual = new Gson().fromJson(body, ErrorResponse.class);
        ErrorResponse expected = new Gson().fromJson(expectedJson, ErrorResponse.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}