package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class GameNotStartedException extends ConflictDomainException {
    public GameNotStartedException() {
        super(GameMessages.GAME_NOT_STARTED);
    }
}
