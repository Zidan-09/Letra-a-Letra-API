package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserNotInGameException extends WebSocketException {
    public UserNotInGameException() {
        super(UserMessages.USER_NOT_IN_GAME);
    }
}
