package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.exception.messages.UserMessages;
import org.springframework.http.HttpStatus;

public class NicknameAlreadyInUseException extends HttpException {
    public NicknameAlreadyInUseException() {
        super(HttpStatus.BAD_REQUEST, UserMessages.NICKNAME_ALREADY_IN_USE);
    }
}
