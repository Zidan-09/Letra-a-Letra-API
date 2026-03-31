package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.exception.WebSocketException;

public class OnlyHostCanBanException extends WebSocketException {
    public OnlyHostCanBanException() {
        super(GameMessages.ONLY_HOST_CAN_BAN);
    }
}
