package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.GameMessages;

public class OnlyHostCanStartException extends WebSocketException {
    public OnlyHostCanStartException() {
        super(GameMessages.ONLY_HOST_CAN_START);
    }
}
