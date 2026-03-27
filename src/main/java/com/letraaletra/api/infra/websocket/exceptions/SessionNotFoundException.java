package com.letraaletra.api.infra.websocket.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.UserMessages;

public class SessionNotFoundException extends WebSocketException {
    public SessionNotFoundException() {
        super(UserMessages.SESSION_NOT_FOUND);
    }
}
