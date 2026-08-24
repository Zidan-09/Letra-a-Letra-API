package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException() {
        super(SecurityMessages.INVALID_CREDENTIALS);
    }
}
