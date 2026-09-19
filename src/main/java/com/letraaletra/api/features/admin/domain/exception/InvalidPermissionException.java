package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidPermissionException extends BadRequestDomainException {
    public InvalidPermissionException() {
        super(AdminMessages.INVALID_PERMISSION);
    }
}
