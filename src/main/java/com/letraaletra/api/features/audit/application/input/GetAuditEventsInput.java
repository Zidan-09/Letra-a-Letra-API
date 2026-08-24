package com.letraaletra.api.features.audit.application.input;

import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.time.Instant;
import java.util.UUID;

public record GetAuditEventsInput(
        AuthenticatedUser principal,
        UUID targetUserId,
        UUID actorId,
        AuditEventType eventType,
        AuditCategory category,
        AuditOutcome outcome,
        AuditResourceType resourceType,
        String resourceId,
        Instant from,
        Instant to,
        String requestId,
        UUID operationId,
        String correlationId,
        UUID transactionId,
        int page,
        int size,
        boolean ascending
) {
    public AuditEventFilter toFilter() {
        return new AuditEventFilter(
                targetUserId,
                actorId,
                eventType,
                category,
                outcome,
                resourceType,
                resourceId,
                from,
                to,
                requestId,
                operationId,
                correlationId,
                transactionId
        );
    }
}
