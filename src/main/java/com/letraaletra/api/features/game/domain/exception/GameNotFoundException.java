package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class GameNotFoundException extends DomainException {
    public GameNotFoundException() {
        super(GameMessages.GAME_NOT_FOUND);
    }
}
