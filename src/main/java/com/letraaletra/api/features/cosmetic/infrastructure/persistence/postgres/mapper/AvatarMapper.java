package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.domain.Avatar;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.AvatarJpaEntity;

public class AvatarMapper {
    public static AvatarJpaEntity toEntity(Avatar avatar) {
        AvatarJpaEntity entity = new AvatarJpaEntity();

        entity.setId(avatar.avatarId());

        return entity;
    }

    public static Avatar toDomain(AvatarJpaEntity entity) {
        return new Avatar(
                entity.getId()
        );
    }
}
