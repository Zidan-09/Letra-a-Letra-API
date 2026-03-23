package com.letraaletra.api.exception;

import org.springframework.http.HttpStatus;

public class HttpException extends AppException {
    private final HttpStatus status;

    public HttpException(HttpStatus status, MessageCode message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
