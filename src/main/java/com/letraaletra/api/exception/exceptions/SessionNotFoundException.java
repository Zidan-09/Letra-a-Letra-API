package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.UserMessages;

public class SessionNotFoundException extends WebSocketException {
    public SessionNotFoundException() {
        super(UserMessages.SESSION_NOT_FOUND);
    }
}
