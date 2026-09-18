package com.letraaletra.api.features.items.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.catalog.ItemFilter;
import com.letraaletra.api.features.items.domain.catalog.ItemsPage;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.jpa.SpringDataItemRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper.ItemJpaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaItemRepository implements ItemRepository, ItemLookup {
    private final SpringDataItemRepository repository;

    public JpaItemRepository(SpringDataItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Item item) {
        repository.save(ItemJpaMapper.toEntity(item));
    }

    @Override
    public Optional<Item> findById(UUID itemId) {
        return repository.findById(itemId).map(ItemJpaMapper::toDomain);
    }

    @Override
    public Optional<Item> findByName(String name) {
        return repository.findByName(name).map(ItemJpaMapper::toDomain);
    }

    @Override
    public Page<Item> findAll(ItemFilter filter, ItemsPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());

        return repository.search(filter.kind(), filter.category(), filter.available(), pageable)
                .map(ItemJpaMapper::toDomain);
    }

    @Override
    public void delete(Item item) {
        repository.deleteById(item.getId());
    }

    @Override
    public Item getById(UUID itemId) {
        return repository.findById(itemId).map(ItemJpaMapper::toDomain)
                .orElseThrow(ItemNotFoundException::new);
    }
}
