package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.GameMessages;

public class CellAlreadyRevealedException extends WebSocketException {
    public CellAlreadyRevealedException() {
        super(GameMessages.CELL_ALREADY_REVEALED);
    }
}
