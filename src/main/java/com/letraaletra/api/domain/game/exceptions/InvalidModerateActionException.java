package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.exception.WebSocketException;

public class InvalidModerateActionException extends WebSocketException {
    public InvalidModerateActionException() {
        super(GameMessages.INVALID_MODERATE_ACTION);
    }
}
