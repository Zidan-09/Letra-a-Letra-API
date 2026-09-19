package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class GameNotRunningException extends ConflictDomainException {
    public GameNotRunningException() {
        super(GameMessages.GAME_NOT_RUNNING);
    }
}
