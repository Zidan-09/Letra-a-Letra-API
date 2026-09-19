package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.features.player.domain.PlayerMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class PlayerIsFrozenException extends ConflictDomainException {
    public PlayerIsFrozenException() {
        super(PlayerMessages.FROZEN_CANNOT_ACT);
    }
}
