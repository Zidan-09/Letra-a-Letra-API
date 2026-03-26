package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.UserMessages;

public class InvalidTokenException extends WebSocketException {
    public InvalidTokenException() {
        super(UserMessages.INVALID_TOKEN);
    }
}
