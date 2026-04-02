package com.letraaletra.api.domain.game.board.cell.exception;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.GameMessages;

public class CellAlreadyRevealedException extends WebSocketException {
    public CellAlreadyRevealedException() {
        super(GameMessages.CELL_ALREADY_REVEALED);
    }
}
