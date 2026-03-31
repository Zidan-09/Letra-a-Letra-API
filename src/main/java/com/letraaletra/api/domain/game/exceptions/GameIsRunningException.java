package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.GameMessages;

public class GameIsRunningException extends WebSocketException {
    public GameIsRunningException() {
        super(GameMessages.GAME_IS_RUNNING);
    }
}
