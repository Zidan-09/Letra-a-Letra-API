package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class UnauthorizedDomainException extends DomainException {
    protected UnauthorizedDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
