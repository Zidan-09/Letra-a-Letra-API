package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserAlreadyWasBannedException extends DomainException {
    public UserAlreadyWasBannedException() {
        super(UserMessages.USER_ALREADY_BANNED);
    }
}
