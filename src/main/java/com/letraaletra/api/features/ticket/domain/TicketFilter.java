package com.letraaletra.api.features.ticket.domain;

import java.util.UUID;

public record TicketFilter(
        TicketStatus status,
        TicketCategory category,
        UUID userId
) {
}
