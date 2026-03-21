package com.letraaletra.api.exception.messages;

import com.letraaletra.api.exception.MessageCode;

public enum GameMessages implements MessageCode {
    GAME_CREATED("game_created"),
    GAME_FOUND("game_found");

    private final String message;

    GameMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
