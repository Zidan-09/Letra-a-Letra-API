package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;

public abstract class PayloadTooLargeDomainException extends DomainException {
    protected PayloadTooLargeDomainException(MessageCode messageCode) {
        super(messageCode);
    }
}
