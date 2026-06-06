package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException() {
        super(UserMessages.INVALID_CREDENTIALS);
    }
}
