package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class TooManyRequestsDomainException extends DomainException {
    protected TooManyRequestsDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
