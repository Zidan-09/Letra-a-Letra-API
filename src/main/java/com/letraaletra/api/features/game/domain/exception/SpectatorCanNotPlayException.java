package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class SpectatorCanNotPlayException extends DomainException {
    public SpectatorCanNotPlayException() {
        super(GameMessages.SPECTATOR_CAN_NOT_PLAY);
    }
}
