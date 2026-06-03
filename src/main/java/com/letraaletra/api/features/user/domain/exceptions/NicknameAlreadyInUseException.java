package com.letraaletra.api.features.user.domain.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class NicknameAlreadyInUseException extends DomainException {
    public NicknameAlreadyInUseException() {
        super(UserMessages.NICKNAME_ALREADY_IN_USE);
    }
}
