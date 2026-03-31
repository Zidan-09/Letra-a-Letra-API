package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.domain.user.UserMessages;
import com.letraaletra.api.exception.WebSocketException;

public class UserBannedException extends WebSocketException {
    public UserBannedException() {
        super(UserMessages.USER_BANNED);
    }
}
