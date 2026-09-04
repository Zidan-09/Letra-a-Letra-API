package com.letraaletra.api.features.audit.domain.repository;

import com.letraaletra.api.features.audit.domain.AuditEventDetails;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FindAuditEvents {
    Page<AuditEventDetails> find(AuditEventFilter filter, int page, int size, boolean ascending);

    boolean existsByTransactionId(UUID transactionId);
}
