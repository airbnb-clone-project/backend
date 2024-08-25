package com.airbnb_clone.common.global.response;

import static lombok.AccessLevel.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = PRIVATE)
@Getter
@Builder(access = PRIVATE)
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;
    
    public static <T> ApiResponse<T> of(ResponseCode responseCode, T data) {
        return ApiResponse.<T>builder()
            .message(responseCode.getMessage())
            .status(responseCode.getStatus())
            .data(data)
            .build();
    }
}