package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataTicketRepository
        extends JpaRepository<TicketJpaEntity, UUID>, JpaSpecificationExecutor<TicketJpaEntity> {
}
