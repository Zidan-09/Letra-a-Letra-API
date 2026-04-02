package com.letraaletra.api.domain.game.player.exception;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.player.PlayerMessages;

public class InvalidPlayerActionException extends WebSocketException {
    public InvalidPlayerActionException() {
        super(PlayerMessages.INVALID_PLAYER_ACTION);
    }
}
