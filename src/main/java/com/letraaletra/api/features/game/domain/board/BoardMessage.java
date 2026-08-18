package com.letraaletra.api.features.game.domain.board;

import com.letraaletra.api.shared.domain.MessageCode;

public enum BoardMessage implements MessageCode {
    INVALID_CELL_POSITION("the provided cell position is invalid");

    private final String message;

    BoardMessage(String message) {
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
