package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class EmailAlreadyInUseException extends ConflictDomainException {
    public EmailAlreadyInUseException() {
        super(AdminMessages.EMAIL_ALREADY_IN_USE);
    }
}
