package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.exception.UnauthorizedDomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class InvalidPasswordException extends UnauthorizedDomainException {
    public InvalidPasswordException() {
        super(SecurityMessages.INVALID_CREDENTIALS);
    }
}
