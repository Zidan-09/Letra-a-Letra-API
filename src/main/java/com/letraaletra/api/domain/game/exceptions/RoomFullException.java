package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.GameMessages;

public class RoomFullException extends WebSocketException {
    public RoomFullException() {
        super(GameMessages.ROOM_FULL);
    }
}
