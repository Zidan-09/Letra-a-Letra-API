package com.letraaletra.api.features.game.domain.board.cell.exception;

import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;
import com.letraaletra.api.features.game.domain.board.BoardMessage;

public class InvalidCellPositionException extends BadRequestDomainException {
    public InvalidCellPositionException() {
        super(BoardMessage.INVALID_CELL_POSITION);
    }
}
