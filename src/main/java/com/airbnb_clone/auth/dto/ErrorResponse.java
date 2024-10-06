package com.airbnb_clone.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.airbnb_clone.auth.dto;
 * fileName       : ErrorResponse
 * author         : DK
 * date           : 24. 8. 22.
 * description    : status, message 를 반환하기 위한 Response DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성, 코드 추가
 */
@Getter @Setter
public class ErrorResponse {
    public static final int UNAUTORIZED_STATUS = 401;
    public static final int OK_STATUS = 200;

    private int status;
    private String message;

    public ErrorResponse() {}
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseEntity<?> ofSuccessBody(String message) {
        ErrorResponse errorResponse = new ErrorResponse(OK_STATUS, message);
        return ResponseEntity
                .ok()
                .body(errorResponse);
    }

    public ResponseEntity<?> ofUnauthorized(String message) {
        ErrorResponse errorResponse = new ErrorResponse(UNAUTORIZED_STATUS, message);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }


}
