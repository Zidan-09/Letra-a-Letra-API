package com.letraaletra.api.features.items.domain.repository;

import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemDefinitionFilter;
import com.letraaletra.api.features.items.domain.ItemsPage;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ItemDefinitionRepository {
    void save(ItemDefinition definition);
    Optional<ItemDefinition> findById(UUID itemId);
    Optional<ItemDefinition> findByName(String name);
    Page<ItemDefinition> findAll(ItemDefinitionFilter filter, ItemsPage page);
    void delete(ItemDefinition definition);
}
