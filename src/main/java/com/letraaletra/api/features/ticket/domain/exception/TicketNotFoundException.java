package com.letraaletra.api.features.ticket.domain.exception;

import com.letraaletra.api.features.ticket.domain.TicketMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class TicketNotFoundException extends DomainException {
    public TicketNotFoundException() {
        super(TicketMessages.TICKET_NOT_FOUND);
    }
}
