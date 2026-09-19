package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class ConflictDomainException extends DomainException {
    protected ConflictDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
