package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.exception.messages.UserMessages;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends HttpException {
    public InvalidPasswordException() {
        super(HttpStatus.FORBIDDEN, UserMessages.INVALID_CREDENTIALS);
    }
}
