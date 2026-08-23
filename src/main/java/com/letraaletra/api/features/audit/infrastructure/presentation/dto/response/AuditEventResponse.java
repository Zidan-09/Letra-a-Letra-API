package com.letraaletra.api.features.audit.infrastructure.presentation.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEventResponse(
        UUID eventId,
        Instant occurredAt,
        String category,
        String eventType,
        String outcome,
        String failureReason,
        String actorType,
        UUID actorId,
        String actorName,
        UUID targetUserId,
        String resourceType,
        String resourceId,
        Map<String, Object> beforeState,
        Map<String, Object> afterState,
        Map<String, Object> delta,
        String reasonCode,
        String requestId,
        UUID operationId,
        String correlationId,
        String sourceType,
        String sourceDetail,
        UUID transactionId,
        Map<String, Object> metadata
) {
}
