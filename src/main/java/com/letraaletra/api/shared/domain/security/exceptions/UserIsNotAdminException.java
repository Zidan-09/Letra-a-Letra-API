package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.SecurityMessages;

public class UserIsNotAdminException extends DomainException {
    public UserIsNotAdminException() {
        super(SecurityMessages.INVALID_USER_DATA);
    }
}
