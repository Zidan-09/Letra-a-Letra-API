package com.letraaletra.api.features.items.domain.repository;

import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.ItemFilter;
import com.letraaletra.api.features.items.domain.ItemsPage;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {
    void save(Item item);
    Optional<Item> findById(UUID itemId);
    Optional<Item> findByName(String name);
    Page<Item> findAll(ItemFilter filter, ItemsPage page);
    void delete(Item item);
}
