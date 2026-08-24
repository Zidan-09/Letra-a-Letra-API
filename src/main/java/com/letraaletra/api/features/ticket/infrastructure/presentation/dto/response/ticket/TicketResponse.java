package com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket;

import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID ticketId,
        UUID userId,
        TicketCategory category,
        TicketStatus status,
        String subject,
        String description,
        String resolutionNote,
        UUID resolvedByAdminId,
        LocalDateTime resolvedAt,
        LocalDateTime createdAt
) {
}
