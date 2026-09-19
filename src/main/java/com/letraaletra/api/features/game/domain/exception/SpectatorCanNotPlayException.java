package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class SpectatorCanNotPlayException extends ForbiddenDomainException {
    public SpectatorCanNotPlayException() {
        super(GameMessages.SPECTATOR_CAN_NOT_PLAY);
    }
}
