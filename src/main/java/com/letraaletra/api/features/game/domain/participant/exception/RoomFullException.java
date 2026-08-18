package com.letraaletra.api.features.game.domain.participant.exception;

import com.letraaletra.api.shared.domain.DomainException;import com.letraaletra.api.features.game.domain.GameMessages;

public class RoomFullException extends DomainException {
    public RoomFullException() {
        super(GameMessages.ROOM_FULL);
    }
}
