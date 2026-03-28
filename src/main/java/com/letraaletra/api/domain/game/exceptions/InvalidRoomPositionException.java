package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.messages.UserMessages;

public class InvalidRoomPositionException extends WebSocketException {
    public InvalidRoomPositionException() {
        super(UserMessages.INVALID_ROOM_POSITION);
    }
}
