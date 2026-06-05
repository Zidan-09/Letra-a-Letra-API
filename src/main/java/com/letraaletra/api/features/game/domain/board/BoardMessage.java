package com.letraaletra.api.features.game.domain.board;

import com.letraaletra.api.shared.domain.MessageCode;

public enum BoardMessage implements MessageCode {
    INVALID_CELL_POSITION("invalid_cell_position");

    private final String message;

    BoardMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
