package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.ServerMessages;

public class InvalidIdException extends WebSocketException {
    public InvalidIdException() {
        super(ServerMessages.INVALID_ID);
    }
}
