package com.letraaletra.api.shared.domain.security.exceptions;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserIsNotAdminException extends DomainException {
    public UserIsNotAdminException() {
        super(UserMessages.INVALID_USER_DATA);
    }
}
