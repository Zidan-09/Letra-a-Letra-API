package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.WalletTransactionJpaEntity;

public class WalletTransactionMapper {
    public static WalletTransaction toDomain(WalletTransactionJpaEntity entity) {
        return new WalletTransaction(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getCoinType(),
                entity.getAmount(),
                entity.getBalanceBefore(),
                entity.getBalanceAfter(),
                entity.getOperation(),
                entity.getReason(),
                entity.getReferenceId(),
                entity.getCreatedAt()
        );
    }

    public static WalletTransactionJpaEntity toEntity(WalletTransaction domain) {
        WalletTransactionJpaEntity entity = new WalletTransactionJpaEntity();

        entity.setTransactionId(domain.transactionId());
        entity.setUserId(domain.userId());
        entity.setCoinType(domain.coinType());
        entity.setAmount(domain.amount());
        entity.setBalanceBefore(domain.balanceBefore());
        entity.setBalanceAfter(domain.balanceAfter());
        entity.setOperation(domain.operation());
        entity.setReason(domain.reason());
        entity.setReferenceId(domain.referenceId());
        entity.setCreatedAt(domain.createdAt());

        return entity;
    }
}
