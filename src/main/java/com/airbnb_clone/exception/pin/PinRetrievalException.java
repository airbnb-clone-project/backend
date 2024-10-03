package com.airbnb_clone.exception.pin;

import com.airbnb_clone.exception.BusinessException;
import com.airbnb_clone.exception.ErrorCode;

/**
 * packageName    : com.airbnb_clone.exception.pin
 * fileName       : PinRetrievalException
 * author         : ipeac
 * date           : 24. 10. 4.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 4.        ipeac       최초 생성
 */
public class PinRetrievalException extends BusinessException {

    public PinRetrievalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
