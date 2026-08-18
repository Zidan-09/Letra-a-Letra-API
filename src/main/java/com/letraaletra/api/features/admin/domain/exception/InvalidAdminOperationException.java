package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidAdminOperationException extends DomainException {
    public InvalidAdminOperationException() {
        super(AdminMessages.INVALID_ADMIN_OPERATION);
    }
}
