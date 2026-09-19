package com.letraaletra.api.features.game.domain.participant.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;import com.letraaletra.api.features.game.domain.GameMessages;

public class RoomFullException extends ConflictDomainException {
    public RoomFullException() {
        super(GameMessages.ROOM_FULL);
    }
}
