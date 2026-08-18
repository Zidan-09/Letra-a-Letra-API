package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;

public class CosmeticMapper {
    public static CosmeticJpaEntity toEntity(Cosmetic cosmetic) {
        CosmeticJpaEntity entity = new CosmeticJpaEntity();

        entity.setId(cosmetic.getId());
        entity.setName(cosmetic.getName());
        entity.setType(cosmetic.getType());
        entity.setAssetPath(cosmetic.getAssetPath());
        entity.setVersion(cosmetic.getVersion());
        entity.setAvailable(cosmetic.isAvailable());

        return entity;
    }

    public static Cosmetic toDomain(CosmeticJpaEntity entity) {
        return Cosmetic.restore(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getAssetPath(),
                entity.getVersion(),
                entity.isAvailable()
        );
    }
}
