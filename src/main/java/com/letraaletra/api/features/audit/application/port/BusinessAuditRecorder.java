package com.letraaletra.api.features.audit.application.port;

import com.letraaletra.api.features.audit.domain.AuditEvent;

public interface BusinessAuditRecorder {
    void record(AuditEvent event);

    void recordFailure(AuditEvent event);
}
