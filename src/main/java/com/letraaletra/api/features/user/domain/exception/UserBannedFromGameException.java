package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserBannedFromGameException extends DomainException {
    public UserBannedFromGameException() {
        super(UserMessages.USER_BANNED_FROM_GAME);
    }
}
