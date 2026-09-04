package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.projection;

import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface AuditEventProjection {

    UUID getEventId();

    Instant getOccurredAt();

    AuditCategory getCategory();

    AuditEventType getEventType();

    AuditOutcome getOutcome();

    String getFailureReason();

    AuditActorType getActorType();

    UUID getActorId();

    String getActorName();

    UUID getTargetUserId();

    String getTargetUsername();

    AuditResourceType getResourceType();

    String getResourceId();

    Map<String, Object> getBeforeState();

    Map<String, Object> getAfterState();

    Map<String, Object> getDelta();

    String getReasonCode();

    String getRequestId();

    UUID getOperationId();

    String getCorrelationId();

    AuditSourceType getSourceType();

    String getSourceDetail();

    UUID getTransactionId();

    Map<String, Object> getMetadata();
}
