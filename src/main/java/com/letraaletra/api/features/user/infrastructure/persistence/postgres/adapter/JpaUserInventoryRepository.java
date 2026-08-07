package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.inventory.InventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserInventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserInventoryJpaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<InventoryItem> getCosmetics(UUID userId) {
        return repository.findInventory(userId).stream()
                .map(UserInventoryJpaMapper::toDomain)
                .toList();
    }

    @Override
    public Page<InventoryItem> getUsersCosmeticsPage(UUID userId, UsersPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findInventoryPage(userId, pageable)
                .map(UserInventoryJpaMapper::toDomain);
    }

    @Override
    public void save(InventoryItem inventory, UUID userId) {
        UserInventoryJpaEntity entity = UserInventoryJpaMapper.toEntity(userId, inventory);
        repository.save(entity);
    }
}