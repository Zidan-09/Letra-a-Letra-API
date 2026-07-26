package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    List<TransactionJpaEntity> findByUserId(UUID userId);
}
