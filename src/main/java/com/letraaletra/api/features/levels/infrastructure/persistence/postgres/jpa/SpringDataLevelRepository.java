package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataLevelRepository extends JpaRepository<LevelJpaEntity, UUID> {
}
