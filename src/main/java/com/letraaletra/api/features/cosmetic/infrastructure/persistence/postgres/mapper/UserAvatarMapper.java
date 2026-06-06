package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.UserAvatarId;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.UserAvatarJpaEntity;

import java.util.UUID;

public class UserAvatarMapper {
    public static UserAvatarJpaEntity toEntity(String userId, String avatarId) {
        UserAvatarJpaEntity entity = new UserAvatarJpaEntity();

        UserAvatarId id = new UserAvatarId();

        id.setUserId(UUID.fromString(userId));
        id.setAvatarId(avatarId);

        entity.setUserAvatarId(id);

        return entity;
    }
}
