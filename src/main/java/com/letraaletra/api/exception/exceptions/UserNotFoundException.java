package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.UserMessages;

public class UserNotFoundException extends WebSocketException {
    public UserNotFoundException() {
        super(UserMessages.USER_NOT_FOUND);
    }
}
