package com.letraaletra.api.features.game.domain.board.theme;

import com.letraaletra.api.shared.domain.MessageCode;

public enum ThemeMessages implements MessageCode {
    FAILED_TO_LOAD_THEMES("failed to load themes"),
    THEME_NOT_FOUND("the theme was not found");

    private final String message;

    ThemeMessages(String message) {
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
