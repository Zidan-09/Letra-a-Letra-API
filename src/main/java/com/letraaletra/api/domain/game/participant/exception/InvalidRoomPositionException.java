package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class InvalidRoomPositionException extends DomainException {
    public InvalidRoomPositionException() {
        super(UserMessages.INVALID_ROOM_POSITION);
    }
}
