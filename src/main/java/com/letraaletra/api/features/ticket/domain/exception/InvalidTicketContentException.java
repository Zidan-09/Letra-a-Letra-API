package com.letraaletra.api.features.ticket.domain.exception;

import com.letraaletra.api.features.ticket.domain.TicketMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidTicketContentException extends DomainException {
    public InvalidTicketContentException() {
        super(TicketMessages.INVALID_TICKET_CONTENT);
    }
}
