package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.exception.messages.GameMessages;
import org.springframework.http.HttpStatus;

public class GameNotFoundException extends HttpException {
    public GameNotFoundException() {
        super(HttpStatus.NOT_FOUND, GameMessages.GAME_NOT_FOUND);
    }
}
