package com.letraaletra.api.features.ticket.domain.exception;

import com.letraaletra.api.features.ticket.domain.TicketMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidTicketContentException extends BadRequestDomainException {
    public InvalidTicketContentException() {
        super(TicketMessages.INVALID_TICKET_CONTENT);
    }
}
