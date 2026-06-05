package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class GameIsRunningException extends DomainException {
    public GameIsRunningException() {
        super(GameMessages.GAME_IS_RUNNING);
    }
}
