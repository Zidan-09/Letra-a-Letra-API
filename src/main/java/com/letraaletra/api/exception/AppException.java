package com.letraaletra.api.exception;

import jakarta.validation.constraints.NotNull;

public class AppException extends RuntimeException {
    private final MessageCode messageCode;

    public AppException(@NotNull MessageCode messageCode) {
        super(messageCode.getMessage());
        this.messageCode = messageCode;
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }
}