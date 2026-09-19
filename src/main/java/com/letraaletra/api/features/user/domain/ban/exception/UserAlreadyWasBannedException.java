package com.letraaletra.api.features.user.domain.ban.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class UserAlreadyWasBannedException extends ConflictDomainException {
    public UserAlreadyWasBannedException() {
        super(UserMessages.USER_ALREADY_BANNED);
    }
}
