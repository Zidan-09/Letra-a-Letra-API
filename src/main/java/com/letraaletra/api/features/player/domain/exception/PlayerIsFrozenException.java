package com.letraaletra.api.features.player.domain.exception;

import com.letraaletra.api.features.player.domain.PlayerMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class PlayerIsFrozenException extends DomainException {
    public PlayerIsFrozenException() {
        super(PlayerMessages.FROZEN_CANNOT_ACT);
    }
}
