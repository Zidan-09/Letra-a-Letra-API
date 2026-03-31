package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.exception.WebSocketException;

public class OnlyHostCanUnbanException extends WebSocketException {
    public OnlyHostCanUnbanException() {
        super(GameMessages.ONLY_HOST_CAN_UNBAN);
    }
}
