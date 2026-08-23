package com.letraaletra.api.features.audit.application.output;

import com.letraaletra.api.features.audit.domain.AuditEvent;
import org.springframework.data.domain.Page;

public record GetAuditEventsOutput(
        Page<AuditEvent> events
) {
}
