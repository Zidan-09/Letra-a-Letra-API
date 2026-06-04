package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.player.domain.PlayerMessages;

public class PlayerNotInGameException extends DomainException {
    public PlayerNotInGameException() {
        super(PlayerMessages.PLAYER_NOT_IN_GAME);
    }
}
