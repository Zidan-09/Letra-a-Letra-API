package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.wallet.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"wallet_log\"")
public class WalletLogJpaEntity {
    @Id
    @Column(name = "log_id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "coin_type")
    private CoinType coinType;

    @Column(name = "amount")
    private int amount;

    @Column(name = "balance_after")
    private int balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private TransactionType transactionType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
