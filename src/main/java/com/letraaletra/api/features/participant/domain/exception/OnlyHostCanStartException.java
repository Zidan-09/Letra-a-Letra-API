package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class OnlyHostCanStartException extends DomainException {
    public OnlyHostCanStartException() {
        super(GameMessages.ONLY_HOST_CAN_START);
    }
}
