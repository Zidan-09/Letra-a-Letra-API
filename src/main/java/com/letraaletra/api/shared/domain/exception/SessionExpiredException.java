package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class SessionExpiredException extends DomainException {
    public SessionExpiredException() {
        super(SecurityMessages.SESSION_EXPIRED);
    }
}
