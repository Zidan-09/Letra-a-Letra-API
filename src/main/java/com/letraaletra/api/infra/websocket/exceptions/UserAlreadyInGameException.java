package com.letraaletra.api.infra.websocket.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.UserMessages;

public class UserAlreadyInGameException extends WebSocketException {
    public UserAlreadyInGameException() {
        super(UserMessages.USER_ALREADY_IN_GAME);
    };
}
