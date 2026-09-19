package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class UserIsNotAdminException extends ForbiddenDomainException {
    public UserIsNotAdminException() {
        super(SecurityMessages.INVALID_USER_DATA);
    }
}
