package com.letraaletra.api.shared.infrastructure.presentation.dto.response;

import com.letraaletra.api.shared.domain.MessageCode;

public enum ServerMessages implements MessageCode {
    INVALID_INPUT("the request contains invalid or malformed data"),
    INTERNAL_ERROR("an unexpected internal server error occurred"),
    SERVICE_UNAVAILABLE("the service is temporarily unavailable"),
    TIMEOUT("the request timed out"),
    UNAUTHORIZED("authentication is required to access this resource"),
    CONFLICT("the request conflicts with the current state of the resource"),
    FORBIDDEN("you do not have permission to access this resource"),
    INVALID_ID("the provided identifier is invalid");

    private final String message;

    ServerMessages(String message) {
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