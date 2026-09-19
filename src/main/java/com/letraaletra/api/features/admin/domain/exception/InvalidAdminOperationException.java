package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidAdminOperationException extends BadRequestDomainException {
    public InvalidAdminOperationException() {
        super(AdminMessages.INVALID_ADMIN_OPERATION);
    }
}
