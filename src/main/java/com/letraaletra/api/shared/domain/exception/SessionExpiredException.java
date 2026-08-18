package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ServerMessages;

public class SessionExpiredException extends DomainException {
    public SessionExpiredException() {
        super(ServerMessages.SESSION_EXPIRED);
    }
}
