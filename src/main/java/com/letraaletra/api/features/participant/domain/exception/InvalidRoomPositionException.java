package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class InvalidRoomPositionException extends BadRequestDomainException {
    public InvalidRoomPositionException() {
        super(UserMessages.INVALID_ROOM_POSITION);
    }
}
