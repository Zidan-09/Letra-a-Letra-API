package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;

public class CosmeticMapper {
    public static CosmeticJpaEntity toEntity(Cosmetic cosmetic) {
        CosmeticJpaEntity entity = new CosmeticJpaEntity();

        entity.setId(cosmetic.id());
        entity.setName(cosmetic.name());
        entity.setType(cosmetic.type());
        entity.setAssetPath(cosmetic.assetPath());
        entity.setVersion(cosmetic.version());

        return entity;
    }

    public static Cosmetic toDomain(CosmeticJpaEntity entity) {
        return new Cosmetic(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getAssetPath(),
                entity.getVersion()
        );
    }
}
