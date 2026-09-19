package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class NicknameAlreadyInUseException extends ConflictDomainException {
    public NicknameAlreadyInUseException() {
        super(UserMessages.NICKNAME_ALREADY_IN_USE);
    }
}
