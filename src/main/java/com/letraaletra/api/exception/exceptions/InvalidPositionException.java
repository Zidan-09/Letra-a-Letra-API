package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.GameMessages;

public class InvalidPositionException extends WebSocketException {
    public InvalidPositionException() {
        super(GameMessages.INVALID_POSITION);
    }
}
