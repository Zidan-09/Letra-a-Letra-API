package com.letraaletra.api.features.audit.domain.repository;

import com.letraaletra.api.features.audit.domain.AuditEvent;

public interface SaveAuditEvent {
    void save(AuditEvent event);
}
