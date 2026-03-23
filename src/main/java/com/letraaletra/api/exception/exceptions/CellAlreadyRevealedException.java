package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.GameMessages;

public class CellAlreadyRevealedException extends WebSocketException {
    public CellAlreadyRevealedException() {
        super(GameMessages.CELL_ALREADY_REVEALED);
    }
}
