package com.airbnb_clone.exception;

import static lombok.AccessLevel.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class ErrorResponse {
    private String message;
    private int status;
    
    @Builder(access = PRIVATE)
    private ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .message(errorCode.getMessage())
            .status(errorCode.getStatus())
            .build();
    }
}