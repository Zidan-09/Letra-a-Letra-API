package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;

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
}
