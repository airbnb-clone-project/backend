package com.airbnb_clone.exception.handler;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.ErrorResponse;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(DuplicateChatRoomException.class)
    protected HttpEntity<ErrorResponse> handleDuplicateChatRoomException(DuplicateChatRoomException e) {
        log.error("handleDuplicateChatRoomException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.DUPLICATE_CHAT_ROOM);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
