package com.letraaletra.api.features.items.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemDefinitionFilter;
import com.letraaletra.api.features.items.domain.ItemDefinitionsPage;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.jpa.SpringDataItemDefinitionRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper.ItemDefinitionJpaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaItemDefinitionRepository implements ItemDefinitionRepository, ItemDefinitionLookup {
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

    @Override
    public Page<ItemDefinition> findAll(ItemDefinitionFilter filter, ItemDefinitionsPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());

        return repository.search(filter.kind(), filter.category(), filter.available(), pageable)
                .map(ItemDefinitionJpaMapper::toDomain);
    }

    @Override
    public void delete(ItemDefinition definition) {
        repository.deleteById(definition.getId());
    }

    @Override
    public ItemDefinition getById(UUID definitionId) {
        return repository.findById(definitionId).map(ItemDefinitionJpaMapper::toDomain)
                .orElseThrow(ItemNotFoundException::new);
    }
}
