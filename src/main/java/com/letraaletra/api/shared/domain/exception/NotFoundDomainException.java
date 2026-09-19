package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class NotFoundDomainException extends DomainException {
    protected NotFoundDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
