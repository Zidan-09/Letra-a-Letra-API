package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelJpaEntity;

import java.util.List;

public class LevelMapper {
    public static Level toDomain(LevelJpaEntity entity, List<LevelReward> rewards) {
        return new Level(
                entity.getId(),
                entity.getLevel(),
                rewards
        );
    }

    public static LevelJpaEntity toEntity(Level domain) {
        LevelJpaEntity entity = new LevelJpaEntity();

        entity.setId(domain.getLevelId());
        entity.setLevel(domain.getLevel());

        return entity;
    }
}
