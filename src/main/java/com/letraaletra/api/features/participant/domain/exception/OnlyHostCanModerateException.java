package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class OnlyHostCanModerateException extends ForbiddenDomainException {
    public OnlyHostCanModerateException() {
        super(GameMessages.ONLY_HOST_CAN_MODERATE);
    }
}
