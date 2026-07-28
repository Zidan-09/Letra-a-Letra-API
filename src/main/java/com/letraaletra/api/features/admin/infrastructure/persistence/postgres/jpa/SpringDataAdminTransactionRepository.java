package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.admin.infrastructure.projection.AdminTransactionProjection;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.entity.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SpringDataAdminTransactionRepository
        extends JpaRepository<TransactionJpaEntity, UUID> {

    @Query("""
    SELECT
        t.transactionId AS transactionId,
        t.userId AS userId,
        u.username AS username,

        t.coinType AS coinType,
        t.amount AS amount,
        t.balanceBefore AS balanceBefore,
        t.balanceAfter AS balanceAfter,

        t.operation AS operation,
        t.reason AS reason,

        t.referenceId AS referenceId,

        CASE
            WHEN t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.SHOP_PURCHASE THEN 'OFFER'
            WHEN t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.LEVEL_UP THEN 'LEVEL'
            ELSE NULL
        END AS referenceType,

        CASE
            WHEN t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.SHOP_PURCHASE THEN o.title
            WHEN t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.LEVEL_UP THEN CONCAT('Nível ', l.level)
            ELSE NULL
        END AS referenceName,

        t.createdAt AS transactionDate

    FROM TransactionJpaEntity t

    JOIN UserJpaEntity u
        ON u.id = t.userId

    LEFT JOIN OfferJpaEntity o
        ON o.id = t.referenceId
        AND t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.SHOP_PURCHASE

    LEFT JOIN LevelJpaEntity l
        ON l.id = t.referenceId
        AND t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.LEVEL_UP
""")
    Page<AdminTransactionProjection> findAllAdmin(Pageable pageable);

}
