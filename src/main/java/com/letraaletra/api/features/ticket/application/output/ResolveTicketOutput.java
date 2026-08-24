package com.letraaletra.api.features.ticket.application.output;

import com.letraaletra.api.features.ticket.domain.Ticket;

public record ResolveTicketOutput(
        Ticket ticket
) {
}
