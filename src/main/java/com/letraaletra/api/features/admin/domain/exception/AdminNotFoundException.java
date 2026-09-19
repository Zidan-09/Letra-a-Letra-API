package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class AdminNotFoundException extends NotFoundDomainException {
    public AdminNotFoundException() {
        super(AdminMessages.ADMIN_NOT_FOUND);
    }
}
