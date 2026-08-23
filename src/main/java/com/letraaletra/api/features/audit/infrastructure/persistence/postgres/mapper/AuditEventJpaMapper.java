package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.entity.AuditEventJpaEntity;

public final class AuditEventJpaMapper {

    private AuditEventJpaMapper() {
    }

    public static AuditEventJpaEntity toEntity(AuditEvent event) {
        AuditEventJpaEntity entity = new AuditEventJpaEntity();

        entity.setEventId(event.eventId());
        entity.setOccurredAt(event.occurredAt());
        entity.setCategory(event.category());
        entity.setEventType(event.eventType());
        entity.setOutcome(event.outcome());
        entity.setFailureReason(event.failureReason());
        entity.setActorType(event.actor().type());
        entity.setActorId(event.actor().id());
        entity.setActorName(event.actor().name());
        entity.setTargetUserId(event.targetUserId());
        entity.setResourceType(event.resourceType());
        entity.setResourceId(event.resourceId());
        entity.setBeforeState(event.beforeState());
        entity.setAfterState(event.afterState());
        entity.setDelta(event.delta());
        entity.setReasonCode(event.reasonCode());
        entity.setRequestId(event.requestId());
        entity.setOperationId(event.operationId());
        entity.setCorrelationId(event.correlationId());
        entity.setSourceType(event.sourceType());
        entity.setSourceDetail(event.sourceDetail());
        entity.setTransactionId(event.transactionId());
        entity.setMetadata(event.metadata());

        return entity;
    }

    public static AuditEvent toDomain(AuditEventJpaEntity entity) {
        return new AuditEvent(
                entity.getEventId(),
                entity.getOccurredAt(),
                entity.getCategory(),
                entity.getEventType(),
                entity.getOutcome(),
                entity.getFailureReason(),
                new AuditActor(entity.getActorType(), entity.getActorId(), entity.getActorName()),
                entity.getTargetUserId(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getBeforeState(),
                entity.getAfterState(),
                entity.getDelta(),
                entity.getReasonCode(),
                entity.getRequestId(),
                entity.getOperationId(),
                entity.getCorrelationId(),
                entity.getSourceType(),
                entity.getSourceDetail(),
                entity.getTransactionId(),
                entity.getMetadata()
        );
    }
}
