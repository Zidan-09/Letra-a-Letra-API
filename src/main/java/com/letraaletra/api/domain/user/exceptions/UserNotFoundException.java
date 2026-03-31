package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserNotFoundException extends WebSocketException {
    public UserNotFoundException() {
        super(UserMessages.USER_NOT_FOUND);
    }
}
