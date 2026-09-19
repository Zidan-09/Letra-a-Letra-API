package com.letraaletra.api.features.ticket.domain.exception;

import com.letraaletra.api.features.ticket.domain.TicketMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class TicketNotFoundException extends NotFoundDomainException {
    public TicketNotFoundException() {
        super(TicketMessages.TICKET_NOT_FOUND);
    }
}
