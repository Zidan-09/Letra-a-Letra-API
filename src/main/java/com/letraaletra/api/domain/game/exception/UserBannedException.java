package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class UserBannedException extends DomainException {
    public UserBannedException() {
        super(UserMessages.USER_BANNED);
    }
}
