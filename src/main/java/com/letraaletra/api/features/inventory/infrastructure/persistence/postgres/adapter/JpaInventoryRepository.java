package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataUserItemRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper.UserItemJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaInventoryRepository implements InventoryRepository {
    private final SpringDataUserItemRepository repository;

    public JpaInventoryRepository(SpringDataUserItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserItem> findItemsByOwner(UUID ownerId) {
        return repository.findItemsByOwner(ownerId).stream()
                .map(UserItemJpaMapper::toDomain)
                .toList();
    }

    @Override
    public void saveItem(UUID ownerId, UserItem item) {
        UserItemJpaEntity entity = UserItemJpaMapper.toEntity(ownerId, item);
        repository.save(entity);
    }

    @Override
    public void deleteItemsByOwner(UUID ownerId) {
        repository.deleteItemsByOwner(ownerId);
    }
}
