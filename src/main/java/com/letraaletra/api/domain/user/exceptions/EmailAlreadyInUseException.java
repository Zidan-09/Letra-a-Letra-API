package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.presentation.messages.UserMessages;
import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends HttpException {
    public EmailAlreadyInUseException() {
        super(HttpStatus.BAD_REQUEST, UserMessages.EMAIL_ALREADY_IN_USE);
    }
}
