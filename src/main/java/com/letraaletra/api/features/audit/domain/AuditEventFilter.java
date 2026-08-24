package com.letraaletra.api.features.audit.domain;

import java.util.UUID;

public record AuditEventFilter(
        UUID targetUserId,
        UUID actorId,
        AuditEventType eventType,
        AuditCategory category,
        AuditOutcome outcome,
        AuditResourceType resourceType,
        String resourceId,
        java.time.Instant from,
        java.time.Instant to,
        String requestId,
        UUID operationId,
        String correlationId,
        UUID transactionId
) {
    public static AuditEventFilter empty() {
        return new AuditEventFilter(
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null
        );
    }
}
