package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class GameNotRunningException extends DomainException {
    public GameNotRunningException() {
        super(GameMessages.GAME_NOT_RUNNING);
    }
}
