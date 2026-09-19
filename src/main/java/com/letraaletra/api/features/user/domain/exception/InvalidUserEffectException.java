package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidUserEffectException extends BadRequestDomainException {
    public InvalidUserEffectException() {
        super(UserMessages.INVALID_USER_EFFECT);
    }
}
