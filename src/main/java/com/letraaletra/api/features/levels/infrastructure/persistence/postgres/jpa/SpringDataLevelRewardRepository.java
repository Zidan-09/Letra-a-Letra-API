package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelRewardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataLevelRewardRepository extends JpaRepository<LevelRewardJpaEntity, UUID> {
    List<LevelRewardJpaEntity> findByLevelId(UUID levelId);
}
