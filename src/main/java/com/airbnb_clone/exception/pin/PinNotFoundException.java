package com.airbnb_clone.exception.pin;

import com.airbnb_clone.exception.BusinessException;
import com.airbnb_clone.exception.ErrorCode;

/**
 * packageName    : com.airbnb_clone.exception.pin
 * fileName       : PinNotFoundException
 * author         : ipeac
 * date           : 24. 8. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 29.        ipeac       최초 생성
 */
public class PinNotFoundException extends BusinessException {
    public PinNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
