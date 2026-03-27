package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.UserMessages;

public class UserNotInGameException extends WebSocketException {
    public UserNotInGameException() {
        super(UserMessages.USER_NOT_IN_GAME);
    }
}
