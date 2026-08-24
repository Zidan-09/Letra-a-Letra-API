package com.letraaletra.api.features.audit.domain.exception;

public class InvalidAuditResourceTypeException extends RuntimeException {
    public InvalidAuditResourceTypeException() {
        super("Invalid audit resource type");
    }
}
