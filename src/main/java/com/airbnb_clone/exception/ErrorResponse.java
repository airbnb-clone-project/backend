package com.airbnb_clone.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ErrorResponse {
    private String message;
    private int status;

    private ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new Builder()
                .message(errorCode.getMessage())
                .status(errorCode.getStatus())
                .build();
    }

    static class Builder {
        private String message;
        private int status;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(message, status);
        }
    }
}
