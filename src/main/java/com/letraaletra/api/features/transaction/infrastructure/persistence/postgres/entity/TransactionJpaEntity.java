package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"transaction\"")
public class TransactionJpaEntity {
    @Id
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "coin_type")
    private CoinType coinType;

    @Column(name = "amount")
    private int amount;

    @Column(name = "balance_before")
    private int balanceBefore;

    @Column(name = "balance_after")
    private int balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private OperationType operation;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private TransactionReason reason;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
