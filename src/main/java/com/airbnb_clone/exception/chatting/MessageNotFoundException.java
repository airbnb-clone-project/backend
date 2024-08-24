package com.airbnb_clone.exception.chatting;

import com.airbnb_clone.exception.BusinessException;
import com.airbnb_clone.exception.ErrorCode;

public class MessageNotFoundException extends BusinessException {

    public MessageNotFoundException() {
        super(ErrorCode.MESSAGE_NOT_FOUND.getMessage());
    }
}
