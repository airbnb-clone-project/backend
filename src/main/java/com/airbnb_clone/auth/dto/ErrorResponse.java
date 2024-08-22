package com.airbnb_clone.auth.dto;

import lombok.Getter;
import lombok.Setter;

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
    private int status;
    private String message;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }


}
