package com.letraaletra.api.features.audit.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record AuditEvent(
        UUID eventId,
        Instant occurredAt,
        AuditCategory category,
        AuditEventType eventType,
        AuditOutcome outcome,
        String failureReason,
        AuditActor actor,
        UUID targetUserId,
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

    private static final int FAILURE_REASON_MAX_LENGTH = 500;

    public AuditEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
        Objects.requireNonNull(category, "category is required");
        Objects.requireNonNull(eventType, "eventType is required");
        Objects.requireNonNull(outcome, "outcome is required");
        Objects.requireNonNull(actor, "actor is required");
        Objects.requireNonNull(actor.type(), "actor type is required");
        Objects.requireNonNull(resourceType, "resourceType is required");
        Objects.requireNonNull(resourceId, "resourceId is required");
        Objects.requireNonNull(sourceType, "sourceType is required");

        if (failureReason != null && failureReason.length() > FAILURE_REASON_MAX_LENGTH) {
            failureReason = failureReason.substring(0, FAILURE_REASON_MAX_LENGTH);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .eventId(eventId)
                .occurredAt(occurredAt)
                .category(category)
                .eventType(eventType)
                .outcome(outcome)
                .failureReason(failureReason)
                .actor(actor)
                .targetUserId(targetUserId)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .beforeState(beforeState)
                .afterState(afterState)
                .delta(delta)
                .reasonCode(reasonCode)
                .requestId(requestId)
                .operationId(operationId)
                .correlationId(correlationId)
                .sourceType(sourceType)
                .sourceDetail(sourceDetail)
                .transactionId(transactionId)
                .metadata(metadata);
    }

    public static class Builder {
        private UUID eventId = UUID.randomUUID();
        private Instant occurredAt = Instant.now();
        private AuditOutcome outcome = AuditOutcome.SUCCESS;
        private AuditCategory category;
        private AuditEventType eventType;
        private String failureReason;
        private AuditActor actor;
        private UUID targetUserId;
        private AuditResourceType resourceType;
        private String resourceId;
        private Map<String, Object> beforeState;
        private Map<String, Object> afterState;
        private Map<String, Object> delta;
        private String reasonCode;
        private String requestId;
        private UUID operationId;
        private String correlationId;
        private AuditSourceType sourceType;
        private String sourceDetail;
        private UUID transactionId;
        private Map<String, Object> metadata;

        public Builder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder occurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder category(AuditCategory category) {
            this.category = category;
            return this;
        }

        public Builder eventType(AuditEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder outcome(AuditOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder actor(AuditActor actor) {
            this.actor = actor;
            return this;
        }

        public Builder targetUserId(UUID targetUserId) {
            this.targetUserId = targetUserId;
            return this;
        }

        public Builder resourceType(AuditResourceType resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Builder resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder beforeState(Map<String, Object> beforeState) {
            this.beforeState = beforeState;
            return this;
        }

        public Builder afterState(Map<String, Object> afterState) {
            this.afterState = afterState;
            return this;
        }

        public Builder delta(Map<String, Object> delta) {
            this.delta = delta;
            return this;
        }

        public Builder reasonCode(String reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder operationId(UUID operationId) {
            this.operationId = operationId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder sourceType(AuditSourceType sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        public Builder sourceDetail(String sourceDetail) {
            this.sourceDetail = sourceDetail;
            return this;
        }

        public Builder transactionId(UUID transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public AuditEvent build() {
            if (category == null) {
                throw new IllegalArgumentException("category is required to build an AuditEvent");
            }
            if (eventType == null) {
                throw new IllegalArgumentException("eventType is required to build an AuditEvent");
            }
            if (actor == null) {
                throw new IllegalArgumentException("actor is required to build an AuditEvent");
            }
            if (resourceType == null) {
                throw new IllegalArgumentException("resourceType is required to build an AuditEvent");
            }
            if (resourceId == null || resourceId.isBlank()) {
                throw new IllegalArgumentException("resourceId is required to build an AuditEvent");
            }
            if (sourceType == null) {
                throw new IllegalArgumentException("sourceType is required to build an AuditEvent");
            }

            return new AuditEvent(
                    eventId,
                    occurredAt,
                    category,
                    eventType,
                    outcome,
                    failureReason,
                    actor,
                    targetUserId,
                    resourceType,
                    resourceId,
                    beforeState == null ? null : Map.copyOf(beforeState),
                    afterState == null ? null : Map.copyOf(afterState),
                    delta == null ? null : Map.copyOf(delta),
                    reasonCode,
                    requestId,
                    operationId,
                    correlationId,
                    sourceType,
                    sourceDetail,
                    transactionId,
                    metadata == null ? null : Map.copyOf(metadata)
            );
        }
    }
}
