package com.letraaletra.api.exception.messages;

import com.letraaletra.api.exception.MessageCode;

public enum CellMessages implements MessageCode {
    ALREADY_REVEALED("already_revealed");

    private final String message;

    CellMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
