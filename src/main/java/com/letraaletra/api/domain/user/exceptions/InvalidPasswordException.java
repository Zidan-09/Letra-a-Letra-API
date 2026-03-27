package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.presentation.messages.UserMessages;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends HttpException {
    public InvalidPasswordException() {
        super(HttpStatus.FORBIDDEN, UserMessages.INVALID_CREDENTIALS);
    }
}
