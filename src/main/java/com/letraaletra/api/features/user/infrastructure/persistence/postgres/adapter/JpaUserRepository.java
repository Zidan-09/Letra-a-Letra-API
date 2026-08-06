package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.*;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserInventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserWalletRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserStatsJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserInventoryJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserWalletJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.InventoryProjection;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;
    private final SpringDataUserInventoryRepository inventoryRepository;
    private final SpringDataUserWalletRepository walletRepository;
    private final SpringDataUserStatsRepository statsRepository;

    @Override
    public void save(User user) {
        repository.save(UserJpaMapper.toEntity(user));

        statsRepository.save(UserStatsJpaMapper.toEntity(user));
        walletRepository.save(UserWalletJpaMapper.toEntity(user));

        List<UserInventoryJpaEntity> inventoryEntities = user.getInventory().getItems().stream()
                .map(item -> UserInventoryJpaMapper.toEntity(user.getUserId(), item))
                .toList();

        inventoryRepository.saveAll(inventoryEntities);
    }

    @Override
    public void saveAll(List<User> users) {
        repository.saveAll(
                users.stream()
                        .map(UserJpaMapper::toEntity)
                        .toList()
        );
    }

    @Override
    public Optional<User> find(UUID id) {
        return repository.findDetailsById(id)
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(id)
                        )
                );
    }

    @Override
    public List<User> findUsersById(List<UUID> ids) {
        List<UserProjection> users = repository.findDetailsByIds(ids);

        List<InventoryProjection> inventories =
                inventoryRepository.findInventoryByUserIds(ids);

        return users.stream()
                .map(user -> UserJpaMapper.toDomain(
                        user,
                        inventories.stream()
                                .filter(item -> item.getUserId().equals(user.getUserId()))
                                .toList()
                ))
                .toList();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findDetailsByUsername(username)
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(projection.getUserId())
                        )
                );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findDetailsByEmail(email)
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(projection.getUserId())
                        )
                );
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return repository.findDetailsByGoogleId(googleId)
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(projection.getUserId())
                        )
                );
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return repository.existsByUsername(nickname);
    }

    @Override
    public long countUsers() {
        return repository.count();
    }

    @Override
    public Page<User> get(UsersPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        Page<UserProjection> users = repository.findDetails(pageable);

        List<UUID> ids = users.stream()
                .map(UserProjection::getUserId)
                .toList();

        Map<UUID, List<InventoryProjection>> inventories =
                inventoryRepository.findInventoryByUserIds(ids)
                        .stream()
                        .collect(Collectors.groupingBy(
                                InventoryProjection::getUserId
                        ));

        return users.map(user ->
                UserJpaMapper.toDomain(
                        user,
                        inventories.getOrDefault(
                                user.getUserId(),
                                List.of()
                        )
                )
        );
    }
}