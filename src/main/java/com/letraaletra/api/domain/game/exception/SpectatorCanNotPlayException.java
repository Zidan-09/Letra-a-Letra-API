package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.exception.WebSocketException;

public class SpectatorCanNotPlayException extends WebSocketException {
    public SpectatorCanNotPlayException() {
        super(GameMessages.SPECTATOR_CAN_NOT_PLAY);
    }
}
