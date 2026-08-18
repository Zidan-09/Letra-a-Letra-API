package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryId;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.InventoryProjection;

import java.util.UUID;

public class UserInventoryJpaMapper {
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

    public static InventoryItem toDomain(InventoryProjection projection) {
        return InventoryItem.restore(
                projection.getCosmeticId(),
                projection.getName(),
                projection.getType(),
                projection.isEquipped(),
                projection.getUnlockedAt()
        );
    }


}