package com.letraaletra.api.exception.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.exception.messages.GameMessages;

public class OnlyHostCanStart extends WebSocketException {
    public OnlyHostCanStart() {
        super(GameMessages.ONLY_HOST_CAN_START);
    }
}
