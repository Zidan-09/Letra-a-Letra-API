package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.GameMessages;

public class NotYourTurnException extends WebSocketException {
    public NotYourTurnException() {
        super(GameMessages.NOT_YOUR_TURN);
    }
}
