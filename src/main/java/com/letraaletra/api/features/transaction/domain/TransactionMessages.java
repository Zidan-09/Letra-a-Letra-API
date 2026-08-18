package com.letraaletra.api.features.transaction.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum TransactionMessages implements MessageCode {
    TRANSACTION_NOT_FOUND("the transaction was not found");

    private final String message;

    TransactionMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
