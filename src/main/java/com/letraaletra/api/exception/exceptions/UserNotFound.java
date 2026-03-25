package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.UserMessages;

public class UserNotFound extends WebSocketException {
    public UserNotFound() {
        super(UserMessages.USER_NOT_FOUND);
    }
}
