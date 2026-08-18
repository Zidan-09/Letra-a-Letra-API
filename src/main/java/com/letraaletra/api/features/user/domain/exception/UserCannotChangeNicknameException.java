package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserCannotChangeNicknameException extends DomainException {
    public UserCannotChangeNicknameException() {
        super(UserMessages.USER_CANNOT_CHANGE_NICKNAME);
    }
}
