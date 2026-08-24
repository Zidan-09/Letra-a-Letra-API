package com.letraaletra.api.features.ticket.application.output;

import com.letraaletra.api.features.ticket.domain.Ticket;
import org.springframework.data.domain.Page;

public record GetMyTicketsOutput(
        Page<Ticket> tickets
) {
}
