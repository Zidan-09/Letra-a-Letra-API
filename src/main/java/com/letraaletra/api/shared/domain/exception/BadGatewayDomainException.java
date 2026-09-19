package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class BadGatewayDomainException extends DomainException {
    protected BadGatewayDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
