package com.letraaletra.api.features.ticket.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum TicketMessages implements MessageCode {
    TICKET_NOT_FOUND("the ticket was not found"),
    INVALID_TICKET_STATUS("the ticket status transition is invalid"),
    INVALID_TICKET_CONTENT("the ticket subject or description is invalid");

    private final String message;

    TicketMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    public String getMessage() {
        return message;
    }
}
