package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryId;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;

import java.util.UUID;

public class UserInventoryMapper {
    public static UserInventoryJpaEntity toEntity(UUID userId, InventoryItem domain) {
        UserInventoryJpaEntity entity = new UserInventoryJpaEntity();
        UserInventoryId id = new UserInventoryId();

        id.setUserId(userId);
        id.setCosmeticId(domain.cosmeticId());

        entity.setUserInventoryId(id);
        entity.setEquipped(domain.equipped());
        entity.setUnlockedAt(domain.unlockedAt());

        return entity;
    }

    public static InventoryItem toDomain(UserInventoryJpaEntity entity, CosmeticJpaEntity cosmeticJpaEntity) {
        return InventoryItem.restore(
                entity.getUserInventoryId().getCosmeticId(),
                cosmeticJpaEntity.getName(),
                cosmeticJpaEntity.getType(),
                entity.isEquipped(),
                entity.getUnlockedAt()
        );
    }
}