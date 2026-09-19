package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.SecurityMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidResetCodeException extends BadRequestDomainException {
    public InvalidResetCodeException() {
        super(SecurityMessages.INVALID_RESET_CODE);
    }
}
