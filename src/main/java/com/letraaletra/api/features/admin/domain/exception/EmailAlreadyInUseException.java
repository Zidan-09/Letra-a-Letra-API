package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class EmailAlreadyInUseException extends DomainException {
    public EmailAlreadyInUseException() {
        super(AdminMessages.EMAIL_ALREADY_IN_USE);
    }
}
