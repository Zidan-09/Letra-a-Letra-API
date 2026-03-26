package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.exception.messages.UserMessages;
import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends HttpException {
    public EmailAlreadyInUseException() {
        super(HttpStatus.BAD_REQUEST, UserMessages.EMAIL_ALREADY_IN_USE);
    }
}
