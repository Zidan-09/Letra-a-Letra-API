package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserWalletJpaEntity;

public class UserWalletMapper {
    public static Wallet toDomain(UserWalletJpaEntity entity) {
        return Wallet.restore(
                entity.getSoftCoins(),
                entity.getHardGems()
        );
    }

    public static UserWalletJpaEntity toEntity(User user) {
        UserWalletJpaEntity entity = new UserWalletJpaEntity();

        Wallet wallet = user.getWallet();

        entity.setUserId(user.getUserId());
        entity.setSoftCoins(wallet.getBalance().coins());
        entity.setHardGems(wallet.getBalance().gems());

        return entity;
    }
}
