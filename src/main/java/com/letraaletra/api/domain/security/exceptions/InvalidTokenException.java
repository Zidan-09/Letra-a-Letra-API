package com.letraaletra.api.domain.security.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.user.UserMessages;

public class InvalidTokenException extends WebSocketException {
    public InvalidTokenException() {
        super(UserMessages.INVALID_TOKEN);
    }
}
