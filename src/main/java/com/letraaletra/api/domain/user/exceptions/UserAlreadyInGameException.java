package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserAlreadyInGameException extends WebSocketException {
    public UserAlreadyInGameException() {
        super(UserMessages.USER_ALREADY_IN_GAME);
    }
}
