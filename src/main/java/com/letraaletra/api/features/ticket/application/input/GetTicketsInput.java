package com.letraaletra.api.features.ticket.application.input;

import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetTicketsInput(
        AuthenticatedUser principal,
        TicketStatus status,
        TicketCategory category,
        UUID userId,
        int page,
        int size,
        Sort sort
) {
    public TicketFilter toFilter() {
        return new TicketFilter(status, category, userId);
    }
}
