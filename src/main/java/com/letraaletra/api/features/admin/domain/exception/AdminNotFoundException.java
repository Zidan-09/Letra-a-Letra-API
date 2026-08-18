package com.letraaletra.api.features.admin.domain.exception;

import com.letraaletra.api.features.admin.domain.AdminMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class AdminNotFoundException extends DomainException {
    public AdminNotFoundException() {
        super(AdminMessages.ADMIN_NOT_FOUND);
    }
}
