package com.letraaletra.api.features.user.domain.reset.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.TooManyRequestsDomainException;

public class MaxAttemptsExceededException extends TooManyRequestsDomainException {
    public MaxAttemptsExceededException() {
        super(UserMessages.MAX_ATTEMPTS_EXCEEDED);
    }
}
