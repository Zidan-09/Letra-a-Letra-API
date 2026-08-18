package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.entity.TransactionJpaEntity;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.projection.TransactionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    String FIND_DETAILS = """
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
                 WHEN t.reason IN (
                     com.letraaletra.api.features.transaction.domain.TransactionReason.ADMIN_GIVE,
                     com.letraaletra.api.features.transaction.domain.TransactionReason.ADMIN_REVOKE
                 ) THEN 'ADMIN'
                 ELSE NULL
             END AS referenceType,

             CASE
                 WHEN t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.SHOP_PURCHASE THEN o.title
                 WHEN t.reason = com.letraaletra.api.features.transaction.domain.TransactionReason.LEVEL_UP THEN CONCAT('Nível ', l.level)
                 WHEN t.reason IN (
                     com.letraaletra.api.features.transaction.domain.TransactionReason.ADMIN_GIVE,
                     com.letraaletra.api.features.transaction.domain.TransactionReason.ADMIN_REVOKE
                 ) THEN a.name
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
        
        LEFT JOIN AdminJpaEntity a
            ON a.id = t.referenceId
           AND t.reason IN (
                com.letraaletra.api.features.transaction.domain.TransactionReason.ADMIN_GIVE,
                com.letraaletra.api.features.transaction.domain.TransactionReason.ADMIN_REVOKE
           )
        """;

    @Query(FIND_DETAILS + " WHERE t.userId = :userId")
    Page<TransactionProjection> findByUserIdDetails(
            UUID userId,
            Pageable pageable
    );

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

    @Query(FIND_DETAILS)
    Page<TransactionProjection> findAllDetails(Pageable pageable);

    @Query(FIND_DETAILS + " WHERE t.transactionId = :transactionId")
    Optional<TransactionProjection> findByIdDetails(
            UUID transactionId
    );
}