package com.letraaletra.api.features.user.domain.reset.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class SamePasswordException extends ConflictDomainException {

    public SamePasswordException() {
        super(UserMessages.SAME_PASSWORD);
    }

}
