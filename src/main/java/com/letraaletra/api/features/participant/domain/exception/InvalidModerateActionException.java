package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class InvalidModerateActionException extends BadRequestDomainException {
    public InvalidModerateActionException() {
        super(GameMessages.INVALID_MODERATE_ACTION);
    }
}
