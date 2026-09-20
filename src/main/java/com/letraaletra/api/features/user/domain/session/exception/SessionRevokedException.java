package com.letraaletra.api.features.user.domain.session.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.UnauthorizedDomainException;

public class SessionRevokedException extends UnauthorizedDomainException {
    public SessionRevokedException() {
        super(UserMessages.SESSION_REVOKED);
    }
}
