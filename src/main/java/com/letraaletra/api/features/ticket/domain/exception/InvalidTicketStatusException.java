package com.letraaletra.api.features.ticket.domain.exception;

import com.letraaletra.api.features.ticket.domain.TicketMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidTicketStatusException extends BadRequestDomainException {
    public InvalidTicketStatusException() {
        super(TicketMessages.INVALID_TICKET_STATUS);
    }
}
