package com.letraaletra.api.features.player.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum PlayerMessages implements MessageCode {
    PLAYER_NOT_FOUND("the player was not found"),
    PLAYER_ALREADY_EXISTS("the player already exists"),
    PLAYER_NOT_IN_GAME("the player is not currently in a game"),

    INVALID_PLAYER_ACTION("the requested player action is invalid"),
    PLAYER_DISCONNECTED("the player has disconnected");

    private final String message;

    PlayerMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    public String getMessage() {
        return message;
    }
}