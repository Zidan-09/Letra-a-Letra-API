package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.UserMessages;

public class UserAlreadyInGame extends WebSocketException {
    public UserAlreadyInGame() {
        super(UserMessages.USER_ALREADY_IN_GAME);
    };
}
