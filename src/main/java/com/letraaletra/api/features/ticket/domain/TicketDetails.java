package com.letraaletra.api.features.ticket.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketDetails(
        UUID ticketId,
        UUID userId,
        String username,

        TicketCategory category,
        TicketStatus status,

        String subject,
        String description,

        String resolutionNote,
        UUID resolvedByAdminId,
        String adminName,

        LocalDateTime resolvedAt,
        LocalDateTime createdAt
) {
}