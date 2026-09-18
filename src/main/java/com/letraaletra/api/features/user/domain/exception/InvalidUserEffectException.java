package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidUserEffectException extends DomainException {
    public InvalidUserEffectException() {
        super(UserMessages.INVALID_USER_EFFECT);
    }
}
