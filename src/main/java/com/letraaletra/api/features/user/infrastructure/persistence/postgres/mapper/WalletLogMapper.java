package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.wallet.WalletLog;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.WalletLogJpaEntity;

import java.util.UUID;

public class WalletLogMapper {
    public static WalletLog toDomain(WalletLogJpaEntity entity) {
        return new WalletLog(
                entity.getId().toString(),
                entity.getCoinType(),
                entity.getAmount(),
                entity.getBalanceAfter(),
                entity.getTransactionType()
        );
    }

    public static WalletLogJpaEntity toEntity(WalletLog domain, String userId) {
        WalletLogJpaEntity entity = new WalletLogJpaEntity();

        entity.setId(UUID.fromString(domain.logId()));
        entity.setUserId(UUID.fromString(userId));
        entity.setCoinType(domain.coinType());
        entity.setAmount(domain.amount());
        entity.setBalanceAfter(domain.balanceAfter());
        entity.setTransactionType(domain.reason());

        return entity;
    }
}
