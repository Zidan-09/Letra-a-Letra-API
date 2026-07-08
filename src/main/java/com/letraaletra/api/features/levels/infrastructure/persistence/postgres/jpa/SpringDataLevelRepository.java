package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataLevelRepository extends JpaRepository<LevelJpaEntity, UUID> {
    Optional<LevelJpaEntity> findByLevel(int level);
    @Query("SELECT MAX(l.level) FROM LevelJpaEntity l")
    Integer findBiggestLevel();
}
