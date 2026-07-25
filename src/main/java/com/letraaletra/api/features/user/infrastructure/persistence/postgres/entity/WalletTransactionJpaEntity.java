package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.wallet.OperationType;
import com.letraaletra.api.features.user.domain.wallet.TransactionReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"wallet_transaction\"")
public class WalletTransactionJpaEntity {
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
