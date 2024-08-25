package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.domain.Message;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.repository.Dto.message.SendMessageRequestDto;
import com.airbnb_clone.chatting.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MessageServiceTest {

    @Autowired
    MongoTemplate mt;

    @SpyBean
    ChatRoomService chatRoomService;

    @SpyBean
    MessageRepository messageRepository;

    @Autowired
    MessageService messageService;

    @BeforeEach
    void beforeEach() {
        mt.dropCollection(ChatRoom.class);
        mt.dropCollection(Message.class);
    }

    @Test
    @DisplayName("메시지 저장 및 조회")
    void save_message() {
        ChatRoomNewResDto chatRoomId = chatRoomService.save(new ChatRoomNewReqDto(List.of(0, 1)));

        String savedId = messageService.save(new SendMessageRequestDto(chatRoomId.getChatRoomId(), 0, "메시지"));

        Message message = messageService.findById(savedId);

        assertThat(message.getNo().toString()).isEqualTo(savedId);
        assertThat(message.getChatRoom().getNo().toString()).isEqualTo(chatRoomId.getChatRoomId());
        assertThat(message.getSenderNo()).isEqualTo(0);
        assertThat(message.getContent()).isEqualTo("메시지");
    }
}
