package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class BadRequestDomainException extends DomainException {
    protected BadRequestDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
