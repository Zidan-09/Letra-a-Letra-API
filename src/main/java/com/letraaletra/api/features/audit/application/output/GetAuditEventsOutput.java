package com.letraaletra.api.features.audit.application.output;

import com.letraaletra.api.features.audit.domain.AuditEventDetails;
import org.springframework.data.domain.Page;

public record GetAuditEventsOutput(
        Page<AuditEventDetails> events
) {
}
