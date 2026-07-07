package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InsufficientBalanceException extends DomainException {
    public InsufficientBalanceException() {
        super(UserMessages.INSUFFICIENT_BALANCE);
    }
}
