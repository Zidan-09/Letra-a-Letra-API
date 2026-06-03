package com.letraaletra.api.shared.domain;

public class DomainException extends RuntimeException {

    public DomainException(MessageCode messageCode) {
        super(messageCode.getMessage());
    }
}