package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.UserMessages;

public class SessionNotFound extends WebSocketException {
    public SessionNotFound() {
        super(UserMessages.SESSION_NOT_FOUND);
    }
}
