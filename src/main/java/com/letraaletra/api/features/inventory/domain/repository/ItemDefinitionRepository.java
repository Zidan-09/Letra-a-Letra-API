package com.letraaletra.api.features.inventory.domain.repository;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;

import java.util.Optional;
import java.util.UUID;

public interface ItemDefinitionRepository {
    void save(ItemDefinition definition);
    Optional<ItemDefinition> findById(UUID itemId);
    Optional<ItemDefinition> findByName(String name);
}
