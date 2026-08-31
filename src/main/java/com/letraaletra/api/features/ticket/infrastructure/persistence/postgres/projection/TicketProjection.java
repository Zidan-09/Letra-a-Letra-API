package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.projection;

import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TicketProjection {

    UUID getTicketId();

    UUID getUserId();

    String getUsername();

    TicketCategory getCategory();

    TicketStatus getStatus();

    String getSubject();

    String getDescription();

    String getResolutionNote();

    UUID getResolvedByAdminId();

    String getAdminName();

    LocalDateTime getResolvedAt();

    LocalDateTime getCreatedAt();
}