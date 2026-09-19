package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.player.domain.PlayerMessages;

public class PlayerNotInGameException extends ConflictDomainException {
    public PlayerNotInGameException() {
        super(PlayerMessages.PLAYER_NOT_IN_GAME);
    }
}
