package com.airbnb_clone.exception.handler;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.ErrorResponse;
import com.airbnb_clone.exception.chatting.ChatRoomNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class StompExceptionHandler {

    @MessageExceptionHandler(ChatRoomNotFoundException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleChatRoomNotFoundException() {
        return ErrorResponse.of(ErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
