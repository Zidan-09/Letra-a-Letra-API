package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class UserAlreadyInGameException extends ConflictDomainException {
    public UserAlreadyInGameException() {
        super(UserMessages.USER_ALREADY_IN_GAME);
    }
}
