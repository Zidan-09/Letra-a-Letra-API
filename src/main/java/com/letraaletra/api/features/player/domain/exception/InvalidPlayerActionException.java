package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.player.domain.PlayerMessages;

public class InvalidPlayerActionException extends DomainException {
    public InvalidPlayerActionException() {
        super(PlayerMessages.INVALID_PLAYER_ACTION);
    }
}
