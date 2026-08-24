package com.letraaletra.api.features.ticket.application.input;

import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record CreateTicketInput(
        AuthenticatedUser principal,
        TicketCategory category,
        String subject,
        String description
) {
}
