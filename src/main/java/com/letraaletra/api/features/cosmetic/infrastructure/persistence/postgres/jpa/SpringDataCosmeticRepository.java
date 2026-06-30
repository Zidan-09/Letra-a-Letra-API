package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCosmeticRepository extends JpaRepository<CosmeticJpaEntity, UUID> {
    Optional<CosmeticJpaEntity> findByName(String name);
}