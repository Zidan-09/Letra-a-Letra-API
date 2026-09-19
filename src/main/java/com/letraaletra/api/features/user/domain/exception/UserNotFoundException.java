package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class UserNotFoundException extends NotFoundDomainException {
    public UserNotFoundException() {
        super(UserMessages.USER_NOT_FOUND);
    }
}
