package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.ItemDefinitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataItemDefinitionRepository extends JpaRepository<ItemDefinitionJpaEntity, UUID> {
    Optional<ItemDefinitionJpaEntity> findByName(String name);
}
