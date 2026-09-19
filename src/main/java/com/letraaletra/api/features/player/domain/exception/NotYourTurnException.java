package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class NotYourTurnException extends ConflictDomainException {
    public NotYourTurnException() {
        super(GameMessages.NOT_YOUR_TURN);
    }
}
