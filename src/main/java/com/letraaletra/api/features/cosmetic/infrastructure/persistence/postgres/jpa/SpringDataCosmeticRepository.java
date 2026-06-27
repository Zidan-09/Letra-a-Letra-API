package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCosmeticRepository extends JpaRepository<CosmeticJpaEntity, String> {
}