package com.airbnb_clone.exception.image;

import com.airbnb_clone.exception.BusinessException;
import com.airbnb_clone.exception.ErrorCode;

/**
 * packageName    : com.airbnb_clone.exception.image
 * fileName       : ContentTypeNotMatchException
 * author         : ipeac
 * date           : 24. 8. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 25.        ipeac       최초 생성
 */
public class ContentTypeNotMatchException extends BusinessException {
    public ContentTypeNotMatchException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
