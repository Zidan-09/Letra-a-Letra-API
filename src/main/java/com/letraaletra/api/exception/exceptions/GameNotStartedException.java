package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.GameMessages;

public class GameNotStartedException extends WebSocketException {
    public GameNotStartedException() {
        super(GameMessages.GAME_NOT_STARTED);
    }
}
