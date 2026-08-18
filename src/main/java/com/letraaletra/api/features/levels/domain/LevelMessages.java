package com.letraaletra.api.features.levels.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum LevelMessages implements MessageCode {
    LEVEL_ALREADY_EXISTS("the level already exists"),
    LEVEL_NOT_FOUND("the level was not found");

    private final String message;

    LevelMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
