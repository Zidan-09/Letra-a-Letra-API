package com.letraaletra.api.exception;

public class WebSocketException extends AppException {
    public WebSocketException(MessageCode message) {
        super(message);
    }
}
