package com.letraaletra.api.exception.messages;

import com.letraaletra.api.exception.MessageCode;

public enum ThemeMessages implements MessageCode {
    THEME_NOT_FOUND("theme_not_found");

    private final String message;

    ThemeMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
