package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super(SecurityMessages.INVALID_TOKEN);
    }
}
