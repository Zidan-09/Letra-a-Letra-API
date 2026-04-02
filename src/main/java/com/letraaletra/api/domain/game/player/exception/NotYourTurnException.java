package com.letraaletra.api.domain.game.player.exception;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.GameMessages;

public class NotYourTurnException extends WebSocketException {
    public NotYourTurnException() {
        super(GameMessages.NOT_YOUR_TURN);
    }
}
