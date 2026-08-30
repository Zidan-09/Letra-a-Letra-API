package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SpringDataTicketRepository
        extends JpaRepository<TicketJpaEntity, UUID>, JpaSpecificationExecutor<TicketJpaEntity> {

    String FIND_TICKET_DETAILS = """
        SELECT t FROM TicketJpaEntity t
        JOIN UserJpaEntity u ON u.id = t.userId
        WHERE u.username = :username
        """;

    @Query(FIND_TICKET_DETAILS)
    Page<TicketJpaEntity> findByUsername(String username, Pageable pageable);
}
