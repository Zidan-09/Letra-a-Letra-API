package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class GameNotStartedException extends DomainException {
    public GameNotStartedException() {
        super(GameMessages.GAME_NOT_STARTED);
    }
}
