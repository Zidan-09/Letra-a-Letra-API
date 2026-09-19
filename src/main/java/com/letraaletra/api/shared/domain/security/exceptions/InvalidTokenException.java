package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.exception.UnauthorizedDomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class InvalidTokenException extends UnauthorizedDomainException {
    public InvalidTokenException() {
        super(SecurityMessages.INVALID_TOKEN);
    }
}
