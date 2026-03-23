package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.GameMessages;

public class NotYourTurnException extends WebSocketException {
    public NotYourTurnException() {
        super(GameMessages.NOT_YOUR_TURN);
    }
}
