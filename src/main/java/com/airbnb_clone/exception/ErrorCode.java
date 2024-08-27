package com.airbnb_clone.exception;

import lombok.Getter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
public enum ErrorCode {
    
    // ChatRoom
    DUPLICATE_CHAT_ROOM(BAD_REQUEST.value(), "이미 존재하는 채팅방입니다."),
    CHAT_ROOM_NOT_FOUND(BAD_REQUEST.value(), "존재하지 않는 채팅방입니다."),

    // Message
    MESSAGE_NOT_FOUND(BAD_REQUEST.value(), "존재하지 않는 메시지입니다.");
    
    private int status;
    private String message;
    
    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}