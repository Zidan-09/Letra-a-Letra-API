package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class OnlyHostCanModerateException extends DomainException {
    public OnlyHostCanModerateException() {
        super(GameMessages.ONLY_HOST_CAN_MODERATE);
    }
}
