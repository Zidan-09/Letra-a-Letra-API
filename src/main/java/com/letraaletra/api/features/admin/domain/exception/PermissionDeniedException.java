package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class PermissionDeniedException extends DomainException {
    public PermissionDeniedException() {
        super(AdminMessages.PERMISSION_DENIED);
    }
}
