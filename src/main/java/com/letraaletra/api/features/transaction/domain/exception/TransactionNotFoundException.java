package com.letraaletra.api.features.transaction.domain.exception;

import com.letraaletra.api.features.transaction.domain.TransactionMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class TransactionNotFoundException extends DomainException {
    public TransactionNotFoundException() {
        super(TransactionMessages.TRANSACTION_NOT_FOUND);
    }
}
