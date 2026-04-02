package com.letraaletra.api.exception;

import com.letraaletra.api.presentation.dto.response.http.ServerMessages;

public class InvalidIdException extends WebSocketException {
    public InvalidIdException() {
        super(ServerMessages.INVALID_ID);
    }
}
