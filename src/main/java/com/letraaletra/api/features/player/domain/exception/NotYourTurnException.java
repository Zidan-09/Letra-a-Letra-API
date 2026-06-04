package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class NotYourTurnException extends DomainException {
    public NotYourTurnException() {
        super(GameMessages.NOT_YOUR_TURN);
    }
}
