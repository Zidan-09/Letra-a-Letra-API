package com.letraaletra.api.features.user.domain.ban.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class UserDoesNotHaveBanException extends ConflictDomainException {
    public UserDoesNotHaveBanException() {
        super(UserMessages.USER_DOES_NOT_HAVE_BAN);
    }
}
