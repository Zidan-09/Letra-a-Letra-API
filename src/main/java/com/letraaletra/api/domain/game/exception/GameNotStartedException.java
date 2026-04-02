package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.GameMessages;

public class GameNotStartedException extends WebSocketException {
    public GameNotStartedException() {
        super(GameMessages.GAME_NOT_STARTED);
    }
}
