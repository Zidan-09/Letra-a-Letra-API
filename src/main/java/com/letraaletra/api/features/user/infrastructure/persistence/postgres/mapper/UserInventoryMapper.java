package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryId;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;

import java.util.UUID;

public class UserInventoryMapper {
    public static UserInventoryJpaEntity toEntity(String userId, InventoryItem domain) {
        UserInventoryJpaEntity entity = new UserInventoryJpaEntity();
        UserInventoryId id = new UserInventoryId();

        id.setUserId(UUID.fromString(userId));
        id.setCosmeticId(domain.cosmetic_id());

        entity.setUserInventoryId(id);
        entity.setEquipped(domain.equipped());
        entity.setUnlockedAt(domain.unlocked_at());

        return entity;
    }

    public static InventoryItem toDomain(UserInventoryJpaEntity entity, CosmeticJpaEntity cosmeticJpaEntity) {
        return new InventoryItem(
                entity.getUserInventoryId().getCosmeticId(),
                cosmeticJpaEntity.getName(),
                cosmeticJpaEntity.getType(),
                entity.isEquipped(),
                entity.getUnlockedAt()
        );
    }
}