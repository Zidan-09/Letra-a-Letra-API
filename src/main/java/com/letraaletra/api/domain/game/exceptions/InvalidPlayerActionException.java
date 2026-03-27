package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.PlayerMessages;

public class InvalidPlayerActionException extends WebSocketException {
    public InvalidPlayerActionException() {
        super(PlayerMessages.INVALID_PLAYER_ACTION);
    }
}
