package com.letraaletra.api.exception.messages;

import com.letraaletra.api.exception.MessageCode;

public enum PlayerMessages implements MessageCode {
    PLAYER_JOINED("player_joined"),
    PLAYER_NOT_FOUND("player_not_found");

    private final String message;

    PlayerMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
