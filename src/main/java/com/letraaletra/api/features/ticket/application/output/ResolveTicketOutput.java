package com.letraaletra.api.features.ticket.application.output;

import com.letraaletra.api.features.ticket.domain.TicketDetails;

public record ResolveTicketOutput(
        TicketDetails ticket
) {
}
