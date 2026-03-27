package com.letraaletra.api.infra.websocket.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.ServerMessages;

public class InvalidIdException extends WebSocketException {
    public InvalidIdException() {
        super(ServerMessages.INVALID_ID);
    }
}
