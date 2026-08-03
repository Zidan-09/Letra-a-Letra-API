package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.InventoryProjection;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.UserProjection;

import java.util.List;

public class UserMapper {
    public static UserJpaEntity toEntity(User user) {
        if (user == null) return null;

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getUserId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setGoogleId(user.getGoogleId());
        entity.setCanChangeNickname(user.canChangeNickname());
        entity.setCurrentGameId(user.getCurrentGameId());
        entity.setCreatedAt(user.getCreatedAt());

        return entity;
    }

    public static User toDomain(UserProjection projection, List<InventoryProjection> inventory) {
        return User.restore(
                projection.getUserId(),
                projection.getUsername(),
                projection.getEmail(),
                projection.getPasswordHash(),
                projection.getGoogleId(),
                projection.getCurrentGameId(),
                projection.isCanChangeNickname(),
                UserStats.restore(
                        projection.getTotalMatches(),
                        projection.getTotalWins(),
                        projection.getWinStreak(),
                        projection.getLevel(),
                        projection.getExperience(),
                        projection.getRankingPoints()
                ),
                Inventory.restore(
                        inventory == null
                                ? List.of()
                                : inventory.stream()
                                .map(UserInventoryMapper::toDomain)
                                .toList()
                ),
                Wallet.restore(
                        projection.getSoftCoins(),
                        projection.getHardGems()
                ),
                projection.getCreatedAt()
        );
    }
}