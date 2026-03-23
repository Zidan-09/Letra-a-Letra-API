package com.letraaletra.api.exception.messages;

import com.letraaletra.api.exception.MessageCode;

public enum ServerMessages implements MessageCode {
    INVALID_INPUT("invalid_input"),
    INTERNAL_ERROR("internal_error"),
    SERVICE_UNAVAILABLE("service_unavailable"),
    TIMEOUT("timeout"),
    UNAUTHORIZED("unauthorized"),
    FORBIDDEN("forbidden");

    private final String message;

    ServerMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}