package com.letraaletra.api.features.audit.application.input;

import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.time.Instant;
import java.util.UUID;

public record GetUserAuditHistoryInput(
        AuthenticatedUser principal,
        UUID userId,
        AuditEventType eventType,
        AuditCategory category,
        Instant from,
        Instant to,
        int page,
        int size
) {
}
