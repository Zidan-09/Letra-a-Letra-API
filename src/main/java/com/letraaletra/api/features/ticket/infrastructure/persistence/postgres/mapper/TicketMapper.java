package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.projection.TicketProjection;

public class TicketMapper {
    public static Ticket toDomain(TicketJpaEntity entity) {
        if (entity == null) return null;

        return Ticket.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getCategory(),
                entity.getSubject(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getResolutionNote(),
                entity.getResolvedByAdminId(),
                entity.getResolvedAt(),
                entity.getCreatedAt()
        );
    }

    public static TicketJpaEntity toEntity(Ticket domain) {
        if (domain == null) return null;

        TicketJpaEntity entity = new TicketJpaEntity();

        entity.setId(domain.getTicketId());
        entity.setUserId(domain.getUserId());
        entity.setCategory(domain.getCategory());
        entity.setStatus(domain.getStatus());
        entity.setSubject(domain.getSubject());
        entity.setDescription(domain.getDescription());
        entity.setResolutionNote(domain.getResolutionNote());
        entity.setResolvedByAdminId(domain.getResolvedByAdminId());
        entity.setResolvedAt(domain.getResolvedAt());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }

    public static TicketDetails toDetails(TicketProjection projection) {
        if (projection == null) return null;

        return new TicketDetails(
                projection.getTicketId(),
                projection.getUserId(),
                projection.getUsername(),
                projection.getCategory(),
                projection.getStatus(),
                projection.getSubject(),
                projection.getDescription(),
                projection.getResolutionNote(),
                projection.getResolvedByAdminId(),
                projection.getAdminName(),
                projection.getResolvedAt(),
                projection.getCreatedAt()
        );
    }

    public static TicketProjection toProjection(Ticket domain) {
        return toProjection(domain, null, null);
    }

    public static TicketProjection toProjection(Ticket domain, String username, String adminName) {
        if (domain == null) return null;

        return new TicketProjectionRecord(
                domain.getTicketId(),
                domain.getUserId(),
                username,
                domain.getCategory(),
                domain.getStatus(),
                domain.getSubject(),
                domain.getDescription(),
                domain.getResolutionNote(),
                domain.getResolvedByAdminId(),
                adminName,
                domain.getResolvedAt(),
                domain.getCreatedAt()
        );
    }

    public record TicketProjectionRecord(
            java.util.UUID ticketId,
            java.util.UUID userId,
            String username,
            com.letraaletra.api.features.ticket.domain.TicketCategory category,
            com.letraaletra.api.features.ticket.domain.TicketStatus status,
            String subject,
            String description,
            String resolutionNote,
            java.util.UUID resolvedByAdminId,
            String adminName,
            java.time.LocalDateTime resolvedAt,
            java.time.LocalDateTime createdAt
    ) implements TicketProjection {
        @Override public java.util.UUID getTicketId() { return ticketId; }
        @Override public java.util.UUID getUserId() { return userId; }
        @Override public String getUsername() { return username; }
        @Override public com.letraaletra.api.features.ticket.domain.TicketCategory getCategory() { return category; }
        @Override public com.letraaletra.api.features.ticket.domain.TicketStatus getStatus() { return status; }
        @Override public String getSubject() { return subject; }
        @Override public String getDescription() { return description; }
        @Override public String getResolutionNote() { return resolutionNote; }
        @Override public java.util.UUID getResolvedByAdminId() { return resolvedByAdminId; }
        @Override public String getAdminName() { return adminName; }
        @Override public java.time.LocalDateTime getResolvedAt() { return resolvedAt; }
        @Override public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    }
}
