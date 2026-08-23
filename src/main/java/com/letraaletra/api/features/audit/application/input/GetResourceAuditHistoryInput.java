package com.letraaletra.api.features.audit.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.time.Instant;
import java.util.UUID;

public record GetResourceAuditHistoryInput(
        AuthenticatedUser principal,
        String resourceType,
        String resourceId,
        UUID targetUserId,
        Instant from,
        Instant to,
        int page,
        int size
) {
}
