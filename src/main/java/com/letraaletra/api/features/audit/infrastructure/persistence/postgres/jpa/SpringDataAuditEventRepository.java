package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.entity.AuditEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataAuditEventRepository
        extends JpaRepository<AuditEventJpaEntity, UUID>, JpaSpecificationExecutor<AuditEventJpaEntity> {

    boolean existsByTransactionId(UUID transactionId);
}
