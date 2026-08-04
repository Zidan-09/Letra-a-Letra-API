package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class MaxAttemptsExceededException extends DomainException {
    public MaxAttemptsExceededException() {
        super(UserMessages.MAX_ATTEMPTS_EXCEEDED);
    }
}
