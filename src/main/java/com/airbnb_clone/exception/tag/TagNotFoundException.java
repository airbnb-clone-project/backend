package com.airbnb_clone.exception.tag;

import com.airbnb_clone.exception.BusinessException;
import com.airbnb_clone.exception.ErrorCode;

/**
 * packageName    : com.airbnb_clone.pin.service
 * fileName       : TagNotFoundException
 * author         : ipeac
 * date           : 24. 9. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 8.        ipeac       최초 생성
 */
public class TagNotFoundException extends BusinessException {
    public TagNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
