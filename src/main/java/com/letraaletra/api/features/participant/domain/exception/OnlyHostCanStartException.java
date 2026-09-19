package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class OnlyHostCanStartException extends ForbiddenDomainException {
    public OnlyHostCanStartException() {
        super(GameMessages.ONLY_HOST_CAN_START);
    }
}
