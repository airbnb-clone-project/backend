package com.airbnb_clone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Chatting
    DUPLICATE_CHAT_ROOM(HttpStatus.BAD_REQUEST, "이미 존재하는 채팅방입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}