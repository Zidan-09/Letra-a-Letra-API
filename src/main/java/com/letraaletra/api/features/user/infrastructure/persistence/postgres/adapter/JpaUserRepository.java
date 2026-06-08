package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserInventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserStatsMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserInventoryMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository repository;
    private final SpringDataUserStatsRepository statsRepository;
    private final SpringDataUserInventoryRepository inventoryRepository;

    public JpaUserRepository(
            SpringDataUserRepository repository,
            SpringDataUserStatsRepository statsRepository,
            SpringDataUserInventoryRepository inventoryRepository
    ) {
        this.repository = repository;
        this.statsRepository = statsRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public User save(User user) {
        repository.save(UserMapper.toEntity(user));
        statsRepository.save(UserStatsMapper.toEntity(user.getStats(), user.getId()));

        List<UserInventoryJpaEntity> inventoryEntities = user.getInventory().stream()
                .map(item -> UserInventoryMapper.toEntity(user.getId(), item))
                .toList();

        inventoryRepository.saveAll(inventoryEntities);

        return user;
    }

    private User assembleUser(UserJpaEntity userEntity) {
        UUID userId = userEntity.getId();

        UserStatsJpaEntity statsEntity = statsRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("UserStats not found for user " + userId));

        List<InventoryItem> inventoryItems = inventoryRepository.findInventoryItemsByUserId(userId);

        return UserMapper.toDomain(userEntity, statsEntity, inventoryItems);
    }

    @Override
    public Optional<User> find(String id) {
        return repository.findById(UUID.fromString(id)).map(this::assembleUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::assembleUser);
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return repository.findByGoogleId(googleId).map(this::assembleUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return repository.existsByUsername(nickname);
    }
}