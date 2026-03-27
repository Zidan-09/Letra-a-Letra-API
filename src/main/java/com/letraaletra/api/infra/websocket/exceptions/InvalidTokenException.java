package com.letraaletra.api.infra.websocket.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.UserMessages;

public class InvalidTokenException extends WebSocketException {
    public InvalidTokenException() {
        super(UserMessages.INVALID_TOKEN);
    }
}
