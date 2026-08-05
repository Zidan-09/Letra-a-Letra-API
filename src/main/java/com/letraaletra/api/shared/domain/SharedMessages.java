package com.letraaletra.api.shared.domain;

public enum SharedMessages implements MessageCode {
    FAILED_TO_SEND_EMAIL("failed to send the email"),
    INVALID_WEBSOCKET_RESPONSE("the websocket response is invalid");

    private final String message;

    SharedMessages(String message) {
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
