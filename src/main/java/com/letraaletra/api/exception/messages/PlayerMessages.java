package com.letraaletra.api.exception.messages;

import com.letraaletra.api.exception.MessageCode;

public enum PlayerMessages implements MessageCode {
    PLAYER_NOT_FOUND("player_not_found"),
    PLAYER_ALREADY_EXISTS("player_already_exists"),
    PLAYER_NOT_IN_GAME("player_not_in_game"),

    INVALID_PLAYER_ACTION("invalid_player_action"),
    PLAYER_DISCONNECTED("player_disconnected"),

    INVALID_PLAYER_NAME("invalid_player_name");

    private final String message;

    PlayerMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}