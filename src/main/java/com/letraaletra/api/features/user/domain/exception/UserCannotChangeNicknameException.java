package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class UserCannotChangeNicknameException extends BadRequestDomainException {
    public UserCannotChangeNicknameException() {
        super(UserMessages.USER_CANNOT_CHANGE_NICKNAME);
    }
}
