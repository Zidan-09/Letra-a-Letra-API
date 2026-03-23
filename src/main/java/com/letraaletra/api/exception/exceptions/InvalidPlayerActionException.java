package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.PlayerMessages;

public class InvalidPlayerActionException extends WebSocketException {
    public InvalidPlayerActionException() {
        super(PlayerMessages.INVALID_PLAYER_ACTION);
    }
}
