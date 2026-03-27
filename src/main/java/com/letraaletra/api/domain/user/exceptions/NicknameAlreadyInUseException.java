package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.exception.HttpException;
import com.letraaletra.api.presentation.messages.UserMessages;
import org.springframework.http.HttpStatus;

public class NicknameAlreadyInUseException extends HttpException {
    public NicknameAlreadyInUseException() {
        super(HttpStatus.BAD_REQUEST, UserMessages.NICKNAME_ALREADY_IN_USE);
    }
}
