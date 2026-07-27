package com.letraaletra.api.shared.domain;

public enum SharedMessages implements MessageCode {
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
