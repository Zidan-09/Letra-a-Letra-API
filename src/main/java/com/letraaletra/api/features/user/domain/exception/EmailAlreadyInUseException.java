package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class EmailAlreadyInUseException extends ConflictDomainException {
    public EmailAlreadyInUseException() {
        super(UserMessages.EMAIL_ALREADY_IN_USE);
    }
}
