package com.letraaletra.api.features.game.domain.board.cell.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class CellAlreadyRevealedException extends DomainException {
    public CellAlreadyRevealedException() {
        super(GameMessages.CELL_ALREADY_REVEALED);
    }
}
