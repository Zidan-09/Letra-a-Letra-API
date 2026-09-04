package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.entity.AuditEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAuditEventRepository
        extends JpaRepository<AuditEventJpaEntity, UUID> {

    String FIND_DETAILS = """
        SELECT
            a.eventId AS eventId,
            a.occurredAt AS occurredAt,
            a.category AS category,
            a.eventType AS eventType,
            a.outcome AS outcome,
            a.failureReason AS failureReason,
            a.actorType AS actorType,
            a.actorId AS actorId,
            a.actorName AS actorName,
            a.targetUserId AS targetUserId,
            u.username AS targetUsername,
            a.resourceType AS resourceType,
            a.resourceId AS resourceId,
            a.beforeState AS beforeState,
            a.afterState AS afterState,
            a.delta AS delta,
            a.reasonCode AS reasonCode,
            a.requestId AS requestId,
            a.operationId AS operationId,
            a.correlationId AS correlationId,
            a.sourceType AS sourceType,
            a.sourceDetail AS sourceDetail,
            a.transactionId AS transactionId,
            a.metadata AS metadata
        FROM AuditEventJpaEntity a
        LEFT JOIN com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity u
            ON u.id = a.targetUserId
        """;

    boolean existsByTransactionId(UUID transactionId);
}
