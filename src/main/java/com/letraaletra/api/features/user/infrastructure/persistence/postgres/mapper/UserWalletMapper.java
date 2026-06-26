package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserWalletJpaEntity;

import java.util.UUID;

public class UserWalletMapper {
    public static Wallet toDomain(UserWalletJpaEntity entity) {
        return new Wallet(
                entity.getSoftCoins(),
                entity.getHard_gems()
        );
    }

    public static UserWalletJpaEntity toEntity(Wallet wallet, UUID userId) {
        UserWalletJpaEntity entity = new UserWalletJpaEntity();

        entity.setUserId(userId);
        entity.setSoftCoins(wallet.getBalance().coins());
        entity.setHard_gems(wallet.getBalance().gems());

        return entity;
    }
}
