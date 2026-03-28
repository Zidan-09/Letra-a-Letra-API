package com.letraaletra.api.exception;

import jakarta.validation.constraints.NotNull;

public class AppException extends RuntimeException {

    public AppException(@NotNull MessageCode messageCode) {
        super(messageCode.getMessage());
    }
}