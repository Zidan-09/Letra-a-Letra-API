package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserDoesNotHaveBanException extends DomainException {
    public UserDoesNotHaveBanException() {
        super(UserMessages.USER_DOES_NOT_HAVE_BAN);
    }
}
