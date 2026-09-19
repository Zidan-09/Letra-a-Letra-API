package com.letraaletra.api.features.game.domain.participant.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class UserNotInGameException extends ConflictDomainException {
    public UserNotInGameException() {
        super(UserMessages.USER_NOT_IN_GAME);
    }
}
