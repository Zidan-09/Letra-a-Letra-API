package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;

public class PermissionDeniedException extends ForbiddenDomainException {
    public PermissionDeniedException() {
        super(AdminMessages.PERMISSION_DENIED);
    }
}
