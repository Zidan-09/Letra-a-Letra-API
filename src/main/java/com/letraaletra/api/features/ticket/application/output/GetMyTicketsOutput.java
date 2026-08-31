package com.letraaletra.api.features.ticket.application.output;

import com.letraaletra.api.features.ticket.domain.TicketDetails;
import org.springframework.data.domain.Page;

public record GetMyTicketsOutput(
        Page<TicketDetails> tickets
) {
}
