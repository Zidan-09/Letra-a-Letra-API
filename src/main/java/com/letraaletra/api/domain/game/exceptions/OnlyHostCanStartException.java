package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.game.GameMessages;

public class OnlyHostCanStartException extends WebSocketException {
    public OnlyHostCanStartException() {
        super(GameMessages.ONLY_HOST_CAN_START);
    }
}
