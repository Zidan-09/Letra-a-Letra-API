package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemId;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.projection.UserItemProjection;

import java.util.UUID;

public class UserItemJpaMapper {
    public static UserItemJpaEntity toEntity(UUID ownerId, UserItem domain) {
        UserItemJpaEntity entity = new UserItemJpaEntity();
        UserItemId id = new UserItemId();

        id.setUserId(ownerId);
        id.setItemId(domain.getItemId());

        entity.setUserItemId(id);
        entity.setQuantity(domain.getQuantity());
        entity.setEquipped(domain.isEquipped());
        entity.setAcquiredAt(domain.getAcquiredAt());
        entity.setExpiresAt(domain.getExpiresAt());

        return entity;
    }

    public static UserItem toDomain(UserItemProjection projection) {
        return UserItem.restore(
                projection.getUserId(),
                projection.getItemId(),
                projection.getQuantity(),
                projection.isEquipped(),
                projection.getAcquiredAt(),
                projection.getExpiresAt()
        );
    }

}
