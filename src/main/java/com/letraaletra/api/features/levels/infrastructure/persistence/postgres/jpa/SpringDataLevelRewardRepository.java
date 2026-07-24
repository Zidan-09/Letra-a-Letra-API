package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelRewardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface SpringDataLevelRewardRepository extends JpaRepository<LevelRewardJpaEntity, UUID> {
    List<LevelRewardJpaEntity> findByLevelId(UUID levelId);

    @Transactional
    @Modifying
    void deleteByLevelId(UUID levelId);
}
