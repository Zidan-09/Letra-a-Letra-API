package com.letraaletra.api.features.game.domain.room.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class InsufficientPlayersException extends ConflictDomainException {
    public InsufficientPlayersException() {
        super(GameMessages.INSUFFICIENT_PLAYERS);
    }
}
