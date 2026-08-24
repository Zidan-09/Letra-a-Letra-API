package com.letraaletra.api.features.audit.infrastructure.presentation.mapper;

import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.infrastructure.presentation.dto.response.AuditEventResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

public final class AuditEventResponseMapper {

    private AuditEventResponseMapper() {
    }

    public static AuditEventResponse toResponse(AuditEvent event) {
        return new AuditEventResponse(
                event.eventId(),
                event.occurredAt(),
                event.category().name(),
                event.eventType().name(),
                event.outcome().name(),
                event.failureReason(),
                event.actor().type().name(),
                event.actor().id(),
                event.actor().name(),
                event.targetUserId(),
                event.resourceType().name(),
                event.resourceId(),
                event.beforeState(),
                event.afterState(),
                event.delta(),
                event.reasonCode(),
                event.requestId(),
                event.operationId(),
                event.correlationId(),
                event.sourceType().name(),
                event.sourceDetail(),
                event.transactionId(),
                event.metadata()
        );
    }

    public static PageResponse<AuditEventResponse> toPageResponse(GetAuditEventsOutput output) {
        var page = output.events();

        return new PageResponse<>(
                page.getContent().stream().map(AuditEventResponseMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
