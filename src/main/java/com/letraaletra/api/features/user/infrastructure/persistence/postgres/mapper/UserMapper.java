package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserWalletJpaEntity;

import java.util.List;

public class UserMapper {
    public static User toDomain(UserJpaEntity entity, UserStatsJpaEntity statsJpa, List<InventoryItem> inventoryDomain, UserWalletJpaEntity walletJpaEntity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getGoogleId(),
                entity.isCanChangeNickname(),
                UserStatsMapper.toDomain(statsJpa),
                new Inventory(inventoryDomain),
                UserWalletMapper.toDomain(walletJpaEntity),
                entity.getCreatedAt()
        );
    }

    public static UserJpaEntity toEntity(User user) {
        if (user == null) return null;

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getNickname());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getHashPassword());
        entity.setGoogleId(user.getGoogleId());
        entity.setCanChangeNickname(user.canChangeNickname());
        entity.setCreatedAt(user.getCreatedAt());

        return entity;
    }
}