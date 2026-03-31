package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.presentation.response.ServerMessages;

public class InvalidIdException extends WebSocketException {
    public InvalidIdException() {
        super(ServerMessages.INVALID_ID);
    }
}
