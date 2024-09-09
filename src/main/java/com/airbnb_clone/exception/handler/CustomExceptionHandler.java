package com.airbnb_clone.exception.handler;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.ErrorResponse;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import com.airbnb_clone.exception.image.ContentTypeNotMatchException;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.exception.tag.TagNotFoundException;
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

    @ExceptionHandler(ContentTypeNotMatchException.class)
    protected HttpEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.CONTENT_TYPE_NOT_MATCH);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PinNotFoundException.class)
    protected HttpEntity<ErrorResponse> handlePinNotFoundException(PinNotFoundException e) {
        log.error("handlePinNotFoundException", e);
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());

        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getHttpStatus()));
    }

    @ExceptionHandler(TagNotFoundException.class)
    protected HttpEntity<ErrorResponse> handleTagNotFoundException(TagNotFoundException e) {
        log.error("handleTagNotFoundException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.TAG_NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
