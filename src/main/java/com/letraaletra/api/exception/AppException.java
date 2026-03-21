package com.letraaletra.api.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException{
    private final HttpStatus status;

    public AppException(HttpStatus status, MessageCode message) {
        super(message.getMessage());
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
