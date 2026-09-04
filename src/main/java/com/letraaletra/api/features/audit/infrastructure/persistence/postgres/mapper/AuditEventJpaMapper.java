package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventDetails;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.entity.AuditEventJpaEntity;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.projection.AuditEventProjection;

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

    public static AuditEventDetails toDetails(AuditEventProjection projection) {
        if (projection == null) return null;

        return new AuditEventDetails(
                projection.getEventId(),
                projection.getOccurredAt(),
                projection.getCategory(),
                projection.getEventType(),
                projection.getOutcome(),
                projection.getFailureReason(),
                new AuditActor(projection.getActorType(), projection.getActorId(), projection.getActorName()),
                projection.getTargetUserId(),
                projection.getTargetUsername(),
                projection.getResourceType(),
                projection.getResourceId(),
                projection.getBeforeState(),
                projection.getAfterState(),
                projection.getDelta(),
                projection.getReasonCode(),
                projection.getRequestId(),
                projection.getOperationId(),
                projection.getCorrelationId(),
                projection.getSourceType(),
                projection.getSourceDetail(),
                projection.getTransactionId(),
                projection.getMetadata()
        );
    }

    public static AuditEventProjection toProjection(AuditEvent event) {
        return toProjection(event, null);
    }

    public static AuditEventProjection toProjection(AuditEvent event, String targetUsername) {
        if (event == null) return null;

        return new AuditEventProjectionRecord(
                event.eventId(),
                event.occurredAt(),
                event.category(),
                event.eventType(),
                event.outcome(),
                event.failureReason(),
                event.actor().type(),
                event.actor().id(),
                event.actor().name(),
                event.targetUserId(),
                targetUsername,
                event.resourceType(),
                event.resourceId(),
                event.beforeState(),
                event.afterState(),
                event.delta(),
                event.reasonCode(),
                event.requestId(),
                event.operationId(),
                event.correlationId(),
                event.sourceType(),
                event.sourceDetail(),
                event.transactionId(),
                event.metadata()
        );
    }

    public static AuditEventDetails toDetails(AuditEvent event, String targetUsername) {
        if (event == null) return null;
        return new AuditEventDetails(
                event.eventId(),
                event.occurredAt(),
                event.category(),
                event.eventType(),
                event.outcome(),
                event.failureReason(),
                event.actor(),
                event.targetUserId(),
                targetUsername,
                event.resourceType(),
                event.resourceId(),
                event.beforeState(),
                event.afterState(),
                event.delta(),
                event.reasonCode(),
                event.requestId(),
                event.operationId(),
                event.correlationId(),
                event.sourceType(),
                event.sourceDetail(),
                event.transactionId(),
                event.metadata()
        );
    }

    public record AuditEventProjectionRecord(
            java.util.UUID eventId,
            java.time.Instant occurredAt,
            com.letraaletra.api.features.audit.domain.AuditCategory category,
            com.letraaletra.api.features.audit.domain.AuditEventType eventType,
            com.letraaletra.api.features.audit.domain.AuditOutcome outcome,
            String failureReason,
            com.letraaletra.api.features.audit.domain.AuditActorType actorType,
            java.util.UUID actorId,
            String actorName,
            java.util.UUID targetUserId,
            String targetUsername,
            com.letraaletra.api.features.audit.domain.AuditResourceType resourceType,
            String resourceId,
            java.util.Map<String, Object> beforeState,
            java.util.Map<String, Object> afterState,
            java.util.Map<String, Object> delta,
            String reasonCode,
            String requestId,
            java.util.UUID operationId,
            String correlationId,
            com.letraaletra.api.features.audit.domain.AuditSourceType sourceType,
            String sourceDetail,
            java.util.UUID transactionId,
            java.util.Map<String, Object> metadata
    ) implements AuditEventProjection {
        @Override public java.util.UUID getEventId() { return eventId; }
        @Override public java.time.Instant getOccurredAt() { return occurredAt; }
        @Override public com.letraaletra.api.features.audit.domain.AuditCategory getCategory() { return category; }
        @Override public com.letraaletra.api.features.audit.domain.AuditEventType getEventType() { return eventType; }
        @Override public com.letraaletra.api.features.audit.domain.AuditOutcome getOutcome() { return outcome; }
        @Override public String getFailureReason() { return failureReason; }
        @Override public com.letraaletra.api.features.audit.domain.AuditActorType getActorType() { return actorType; }
        @Override public java.util.UUID getActorId() { return actorId; }
        @Override public String getActorName() { return actorName; }
        @Override public java.util.UUID getTargetUserId() { return targetUserId; }
        @Override public String getTargetUsername() { return targetUsername; }
        @Override public com.letraaletra.api.features.audit.domain.AuditResourceType getResourceType() { return resourceType; }
        @Override public String getResourceId() { return resourceId; }
        @Override public java.util.Map<String, Object> getBeforeState() { return beforeState; }
        @Override public java.util.Map<String, Object> getAfterState() { return afterState; }
        @Override public java.util.Map<String, Object> getDelta() { return delta; }
        @Override public String getReasonCode() { return reasonCode; }
        @Override public String getRequestId() { return requestId; }
        @Override public java.util.UUID getOperationId() { return operationId; }
        @Override public String getCorrelationId() { return correlationId; }
        @Override public com.letraaletra.api.features.audit.domain.AuditSourceType getSourceType() { return sourceType; }
        @Override public String getSourceDetail() { return sourceDetail; }
        @Override public java.util.UUID getTransactionId() { return transactionId; }
        @Override public java.util.Map<String, Object> getMetadata() { return metadata; }
    }
}
