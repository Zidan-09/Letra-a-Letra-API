package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class AlreadyHaveThisPermissionException extends ConflictDomainException {
    public AlreadyHaveThisPermissionException() {
        super(AdminMessages.ALREADY_HAVE_THIS_PERMISSION);
    }
}
