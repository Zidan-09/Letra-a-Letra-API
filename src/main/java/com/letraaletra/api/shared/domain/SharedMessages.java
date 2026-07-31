package com.letraaletra.api.shared.domain;

public enum SharedMessages implements MessageCode {
    ERROR_TO_SEND_EMAIL("error_to_send_email"),
    INVALID_WEBSOCKET_RESPONSE("invalid_websocket_response");

    private final String message;

    SharedMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
