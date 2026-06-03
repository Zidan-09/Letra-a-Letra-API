package com.letraaletra.api.features.user.domain.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class EmailAlreadyInUseException extends DomainException {
    public EmailAlreadyInUseException() {
        super(UserMessages.EMAIL_ALREADY_IN_USE);
    }
}
