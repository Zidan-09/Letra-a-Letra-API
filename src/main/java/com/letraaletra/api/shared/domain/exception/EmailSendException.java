package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.SharedMessages;

public class EmailSendException extends DomainException {
    public EmailSendException() {
        super(SharedMessages.ERROR_TO_SEND_EMAIL);
    }
}
