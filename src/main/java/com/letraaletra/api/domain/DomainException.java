package com.letraaletra.api.domain;

import jakarta.validation.constraints.NotNull;

public class DomainException extends RuntimeException {

    public DomainException(@NotNull MessageCode messageCode) {
        super(messageCode.getMessage());
    }
}