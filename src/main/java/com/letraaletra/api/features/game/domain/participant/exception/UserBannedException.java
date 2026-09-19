package com.letraaletra.api.features.game.domain.participant.exception;

import com.letraaletra.api.features.game.domain.GameMessages;
import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;

public class UserBannedException extends ForbiddenDomainException {
    public UserBannedException() {
        super(GameMessages.YOU_ARE_BANNED_OF_THIS_ROOM);
    }
}
