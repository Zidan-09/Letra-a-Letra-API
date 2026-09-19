package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class GameIsRunningException extends ConflictDomainException {
    public GameIsRunningException() {
        super(GameMessages.GAME_IS_RUNNING);
    }
}
