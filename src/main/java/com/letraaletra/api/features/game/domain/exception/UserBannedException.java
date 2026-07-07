package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.features.game.domain.GameMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserBannedException extends DomainException {
    public UserBannedException() {
        super(GameMessages.YOU_ARE_BANNED_OF_THIS_ROOM);
    }
}
