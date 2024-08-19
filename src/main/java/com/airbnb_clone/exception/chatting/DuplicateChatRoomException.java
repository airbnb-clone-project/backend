package com.airbnb_clone.exception.chatting;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.BusinessException;

public class DuplicateChatRoomException extends BusinessException {

    public DuplicateChatRoomException() {
        super(ErrorCode.DUPLICATE_CHAT_ROOM.getMessage());
    }
}
