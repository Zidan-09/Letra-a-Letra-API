package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataItemDefinitionRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper.ItemDefinitionJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaItemDefinitionRepository implements ItemDefinitionRepository {
    private final SpringDataItemDefinitionRepository repository;

    public JpaItemDefinitionRepository(SpringDataItemDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(ItemDefinition definition) {
        repository.save(ItemDefinitionJpaMapper.toEntity(definition));
    }

    @Override
    public Optional<ItemDefinition> findById(UUID itemId) {
        return repository.findById(itemId).map(ItemDefinitionJpaMapper::toDomain);
    }

    @Override
    public Optional<ItemDefinition> findByName(String name) {
        return repository.findByName(name).map(ItemDefinitionJpaMapper::toDomain);
    }
}
