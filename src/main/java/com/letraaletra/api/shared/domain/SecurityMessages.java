package com.letraaletra.api.shared.domain;

public enum SecurityMessages implements MessageCode {
    INVALID_CREDENTIALS("the provided credentials are invalid"),
    INVALID_TOKEN("the provided token is invalid"),
    INVALID_USER_DATA("the provided user data is invalid"),
    SESSION_EXPIRED("the session has expired or was invalidated by another login");

    private final String message;

    SecurityMessages(String message) {
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
