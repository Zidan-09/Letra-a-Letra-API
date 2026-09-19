package com.letraaletra.api.features.user.domain.wallet.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class InsufficientBalanceException extends ConflictDomainException {
    public InsufficientBalanceException() {
        super(UserMessages.INSUFFICIENT_BALANCE);
    }
}
