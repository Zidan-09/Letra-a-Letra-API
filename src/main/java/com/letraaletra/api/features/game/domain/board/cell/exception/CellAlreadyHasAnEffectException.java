package com.letraaletra.api.features.game.domain.board.cell.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class CellAlreadyHasAnEffectException extends ConflictDomainException {
    public CellAlreadyHasAnEffectException() {
        super(GameMessages.CELL_ALREADY_HAS_AN_EFFECT);
    }
}
