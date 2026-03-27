package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.PlayerMessages;

public class PlayerNotInGameException extends WebSocketException {
    public PlayerNotInGameException() {
        super(PlayerMessages.PLAYER_NOT_IN_GAME);
    }
}
