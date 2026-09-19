package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class ForbiddenDomainException extends DomainException {
    protected ForbiddenDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
