package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.entity.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    Page<TransactionJpaEntity> findByUserId(UUID userId, Pageable pageable);
    @Query("""
        SELECT COUNT(t) > 0
        FROM TransactionJpaEntity t
        WHERE t.userId = :userId
          AND t.referenceId = :referenceId
          AND t.reason = :reason
    """)
        boolean hasPurchasedOffer(
                UUID userId,
                UUID referenceId,
                TransactionReason reason
        );
}
