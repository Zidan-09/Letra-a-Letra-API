package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.exception.BadGatewayDomainException;
import com.letraaletra.api.shared.domain.SharedMessages;

public class EmailSendException extends BadGatewayDomainException {
    public EmailSendException() {
        super(SharedMessages.FAILED_TO_SEND_EMAIL);
    }
}
