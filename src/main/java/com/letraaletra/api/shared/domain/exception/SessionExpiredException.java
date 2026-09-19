package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.exception.UnauthorizedDomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class SessionExpiredException extends UnauthorizedDomainException {
    public SessionExpiredException() {
        super(SecurityMessages.SESSION_EXPIRED);
    }
}
