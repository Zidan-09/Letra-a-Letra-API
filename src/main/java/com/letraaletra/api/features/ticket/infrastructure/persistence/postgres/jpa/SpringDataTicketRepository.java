package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.projection.TicketProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataTicketRepository
        extends JpaRepository<TicketJpaEntity, UUID>, JpaSpecificationExecutor<TicketJpaEntity> {

    String FIND_DETAILS = """
        SELECT
            t.ticket_id AS ticketId,
            t.user_id AS userId,
            u.username AS username,

            t.category AS category,
            t.status AS status,

            t.subject AS subject,
            t.description AS description,
            t.resolution_note AS resolutionNote,

            t.resolved_by_admin_id AS resolvedByAdminId,
            a.name AS adminName,

            t.resolved_at AS resolvedAt,
            t.created_at AS createdAt

        FROM "ticket" t

        JOIN "user" u
            ON u.user_id = t.user_id

        LEFT JOIN "admin" a
            ON a.admin_id = t.resolved_by_admin_id
    """;

    @Query(value = FIND_DETAILS + " WHERE t.user_id = :userId", nativeQuery = true)
    Page<TicketProjection> findByUserIdDetails(
            UUID userId,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.ticket_id = :ticketId", nativeQuery = true)
    Optional<TicketProjection> findByIdDetails(
            UUID ticketId
    );

    @Query(value = FIND_DETAILS, nativeQuery = true)
    Page<TicketProjection> findAllDetails(Pageable pageable);

    @Query(value = FIND_DETAILS + " WHERE t.category = :category AND t.status = :status AND t.user_id = :userId", nativeQuery = true)
    Page<TicketProjection> findByCategoryStatusUserId(
            String category,
            String status,
            UUID userId,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.category = :category AND t.user_id = :userId", nativeQuery = true)
    Page<TicketProjection> findByCategoryUserId(
            String category,
            UUID userId,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.status = :status AND t.user_id = :userId", nativeQuery = true)
    Page<TicketProjection> findByStatusUserId(
            String status,
            UUID userId,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.user_id = :userId", nativeQuery = true)
    Page<TicketProjection> findByUserId(
            UUID userId,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.category = :category AND t.status = :status", nativeQuery = true)
    Page<TicketProjection> findByCategoryStatus(
            String category,
            String status,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.category = :category", nativeQuery = true)
    Page<TicketProjection> findByCategory(
            String category,
            Pageable pageable
    );

    @Query(value = FIND_DETAILS + " WHERE t.status = :status", nativeQuery = true)
    Page<TicketProjection> findByStatus(
            String status,
            Pageable pageable
    );

    String FIND_BY_USERNAME = """
        SELECT
            t.ticket_id AS ticketId,
            t.user_id AS userId,
            u.username AS username,

            t.category AS category,
            t.status AS status,

            t.subject AS subject,
            t.description AS description,
            t.resolution_note AS resolutionNote,

            t.resolved_by_admin_id AS resolvedByAdminId,
            a.name AS adminName,

            t.resolved_at AS resolvedAt,
            t.created_at AS createdAt

        FROM "ticket" t

        JOIN "user" u
            ON u.user_id = t.user_id

        LEFT JOIN "admin" a
            ON a.admin_id = t.resolved_by_admin_id

        WHERE u.username = :username
    """;

    @Query(value = FIND_BY_USERNAME, nativeQuery = true)
    Page<TicketProjection> findByUsername(String username, Pageable pageable);
}
