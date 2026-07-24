package com.letraaletra.api.features.levels.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum LevelMessages implements MessageCode {
    LEVEL_ALREADY_EXISTS("level_already_exists"),
    LEVEL_NOT_FOUND("level_not_found");

    private final String message;

    LevelMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
