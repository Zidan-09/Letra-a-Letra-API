package com.letraaletra.api.features.user.domain.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException() {
        super(UserMessages.USER_NOT_FOUND);
    }
}
