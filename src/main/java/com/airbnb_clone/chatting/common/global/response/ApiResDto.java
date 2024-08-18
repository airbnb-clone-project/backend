package com.airbnb_clone.chatting.common.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ApiResDto<T> {
    
    private String message;
    private int status;
    private T data;
}
