package com.letraaletra.api.features.game.domain.board.cell.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class CellAlreadyHasAnEffectException extends DomainException {
    public CellAlreadyHasAnEffectException() {
        super(GameMessages.CELL_ALREADY_HAS_AN_EFFECT);
    }
}
