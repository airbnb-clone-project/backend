package com.airbnb_clone.common.global.response;

import static lombok.AccessLevel.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = PRIVATE)
@Getter
@Builder
public class ApiResponse<T> {
    private String message;
    private HttpStatus status;
    private T data;
    
    public static <T> ApiResponse<T> of(String message, HttpStatus status, T data) {
        return ApiResponse.<T>builder()
            .message(message)
            .status(status)
            .data(data)
            .build();
    }
}