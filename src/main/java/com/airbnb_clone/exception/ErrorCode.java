package com.airbnb_clone.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    // Chatting
    DUPLICATE_CHAT_ROOM(400, "이미 존재하는 채팅방입니다.");
    
    private int status;
    private String message;
    
    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}