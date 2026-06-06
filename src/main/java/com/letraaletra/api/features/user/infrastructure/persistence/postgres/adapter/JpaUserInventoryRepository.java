package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.InventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserInventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserInventoryMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaUserInventoryRepository implements InventoryRepository {
    private final SpringDataUserInventoryRepository repository;

    public JpaUserInventoryRepository(SpringDataUserInventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<InventoryItem> getCosmetics(String userId) {
        return repository.findInventoryItemsByUserId(UUID.fromString(userId));
    }

    @Override
    public InventoryItem save(InventoryItem inventory, String userId) {
        var entity = UserInventoryMapper.toEntity(userId, inventory);
        repository.save(entity);
        return inventory;
    }

    @Override
    public InventoryItem unlockCosmetic(String cosmeticId, String userId) {
        return null;
    }
}