package com.letraaletra.api.features.user.domain.ban.exception;

import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;

public class UserBannedFromGameException extends ForbiddenDomainException {
    public UserBannedFromGameException() {
        super(UserMessages.USER_BANNED_FROM_GAME);
    }
}
