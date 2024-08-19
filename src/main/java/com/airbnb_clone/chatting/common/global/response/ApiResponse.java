package com.airbnb_clone.chatting.common.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;

    public static <T> ApiResponse<T> of(String message, int status, T data) {
        return new Builder<T>()
                .message(message)
                .status(status)
                .data(data)
                .build();
    }

    static class Builder<T> {
        private String message;
        private int status;
        private T data;

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> status(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(message, status, data);
        }
    }
}