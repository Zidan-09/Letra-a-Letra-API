package com.letraaletra.api.features.transaction.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum TransactionMessages implements MessageCode {
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND");

    private final String message;

    TransactionMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
