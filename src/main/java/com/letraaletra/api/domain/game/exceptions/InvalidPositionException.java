package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.GameMessages;

public class InvalidPositionException extends WebSocketException {
    public InvalidPositionException() {
        super(GameMessages.INVALID_POSITION);
    }
}
