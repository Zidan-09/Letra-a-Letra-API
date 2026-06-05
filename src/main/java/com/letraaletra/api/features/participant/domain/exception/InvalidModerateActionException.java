package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class InvalidModerateActionException extends DomainException {
    public InvalidModerateActionException() {
        super(GameMessages.INVALID_MODERATE_ACTION);
    }
}
