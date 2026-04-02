package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.domain.game.GameMessages;
import org.springframework.http.HttpStatus;

public class GameNotFoundException extends HttpException {
    public GameNotFoundException() {
        super(HttpStatus.NOT_FOUND, GameMessages.GAME_NOT_FOUND);
    }
}
