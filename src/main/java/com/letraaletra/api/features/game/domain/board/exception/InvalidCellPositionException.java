package com.letraaletra.api.features.game.domain.board.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.board.BoardMessage;

public class InvalidCellPositionException extends DomainException {
    public InvalidCellPositionException() {
        super(BoardMessage.INVALID_CELL_POSITION);
    }
}
