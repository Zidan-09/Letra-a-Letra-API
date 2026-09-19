package com.letraaletra.api.features.transaction.domain.exception;

import com.letraaletra.api.features.transaction.domain.TransactionMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class TransactionNotFoundException extends NotFoundDomainException {
    public TransactionNotFoundException() {
        super(TransactionMessages.TRANSACTION_NOT_FOUND);
    }
}
