package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.user.UserMessages;
import com.letraaletra.api.exception.WebSocketException;

public class UserBannedException extends WebSocketException {
    public UserBannedException() {
        super(UserMessages.USER_BANNED);
    }
}
