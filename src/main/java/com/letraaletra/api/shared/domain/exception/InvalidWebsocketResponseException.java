package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.SharedMessages;

public class InvalidWebsocketResponseException extends DomainException {
    public InvalidWebsocketResponseException() {
        super(SharedMessages.INVALID_WEBSOCKET_RESPONSE);
    }
}
