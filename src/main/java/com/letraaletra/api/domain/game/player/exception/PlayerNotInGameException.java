package com.letraaletra.api.domain.game.player.exception;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.player.PlayerMessages;

public class PlayerNotInGameException extends WebSocketException {
    public PlayerNotInGameException() {
        super(PlayerMessages.PLAYER_NOT_IN_GAME);
    }
}
