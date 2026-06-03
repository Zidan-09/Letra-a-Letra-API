package com.letraaletra.api.domain.game.board.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.domain.game.board.BoardMessage;

public class InvalidCellPositionException extends DomainException {
    public InvalidCellPositionException() {
        super(BoardMessage.INVALID_CELL_POSITION);
    }
}
