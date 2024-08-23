package com.airbnb_clone.exception.chatting;

import com.airbnb_clone.exception.BusinessException;
import com.airbnb_clone.exception.ErrorCode;

public class ChatRoomNotFoundException extends BusinessException {

    public ChatRoomNotFoundException() {
        super(ErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
    }
}
