package com.letraaletra.api.features.audit.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEventDetails(
        UUID eventId,
        Instant occurredAt,
        AuditCategory category,
        AuditEventType eventType,
        AuditOutcome outcome,
        String failureReason,
        AuditActor actor,
        UUID targetUserId,
        String targetUsername,
        AuditResourceType resourceType,
        String resourceId,
        Map<String, Object> beforeState,
        Map<String, Object> afterState,
        Map<String, Object> delta,
        String reasonCode,
        String requestId,
        UUID operationId,
        String correlationId,
        AuditSourceType sourceType,
        String sourceDetail,
        UUID transactionId,
        Map<String, Object> metadata
) {
}
