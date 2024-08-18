package com.airbnb_clone.chatting.controller;

import com.airbnb_clone.chatting.common.global.response.ApiResDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.service.ChatRoomService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        mockMvc = MockMvcBuilders.standaloneSetup(chatRoomController).build();
    }

    @Test
    @DisplayName("채팅방 저장")
    void chat_room_save_and_find() throws Exception {
        ObjectId objectId = new ObjectId(new Date());
        ChatRoomNewReqDto chatRoomNewReqDto = new ChatRoomNewReqDto(new ArrayList<>(List.of(0, 1)));
        ChatRoomNewResDto chatRoomNewResDto = new ChatRoomNewResDto(objectId.toString());

        ApiResDto<ChatRoomNewResDto> resApi = ApiResDto.<ChatRoomNewResDto>builder()
                .status(200)
                .message("생성 성공!")
                .data(chatRoomNewResDto)
                .build();
        String expectedJson = new Gson().toJson(resApi);

        doReturn(chatRoomNewResDto).when(chatRoomService).save(any(ChatRoomNewReqDto.class));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/chat-room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(chatRoomNewReqDto)));

        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        ApiResDto actual = new Gson().fromJson(body, ApiResDto.class);
        ApiResDto expected = new Gson().fromJson(expectedJson, ApiResDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}