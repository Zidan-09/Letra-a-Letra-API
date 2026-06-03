package com.letraaletra.api.features.user.domain.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class UserAlreadyInGameException extends DomainException {
    public UserAlreadyInGameException() {
        super(UserMessages.USER_ALREADY_IN_GAME);
    }
}
