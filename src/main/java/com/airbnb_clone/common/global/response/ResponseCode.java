package com.airbnb_clone.common.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    // Chatting
    CREATE_CHAT_ROOM( "채팅방 생성 완료!", HttpStatus.CREATED.value()),
    SEARCH_USER_CHAT_ROOMS("채팅방 조회 완료!", HttpStatus.OK.value());

    private String message;
    private int status;

    ResponseCode(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
