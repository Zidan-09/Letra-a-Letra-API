package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidPermissionException extends DomainException {
    public InvalidPermissionException() {
        super(AdminMessages.INVALID_PERMISSION);
    }
}
