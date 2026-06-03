package com.letraaletra.api.domain.security.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super(UserMessages.INVALID_TOKEN);
    }
}
