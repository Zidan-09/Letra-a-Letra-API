package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class SamePasswordException extends DomainException {

    public SamePasswordException() {
        super(UserMessages.SAME_PASSWORD);
    }

}
