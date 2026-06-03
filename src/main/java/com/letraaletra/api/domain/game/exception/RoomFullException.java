package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.shared.domain.DomainException;import com.letraaletra.api.domain.game.GameMessages;

public class RoomFullException extends DomainException {
    public RoomFullException() {
        super(GameMessages.ROOM_FULL);
    }
}
