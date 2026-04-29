package com.letraaletra.api.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserStatsJpaEntity;
import com.letraaletra.api.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.mapper.UserMapper;
import com.letraaletra.api.infrastructure.persistence.postgres.mapper.UserStatsMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository repository;
    private final SpringDataUserStatsRepository statsRepository;

    public JpaUserRepository(SpringDataUserRepository repository, SpringDataUserStatsRepository statsRepository) {
        this.repository = repository;
        this.statsRepository = statsRepository;
    }

    @Override
    public User save(User user) {
        repository.save(UserMapper.toEntity(user));
        statsRepository.save(UserStatsMapper.toEntity(user.getStats(), user.getId()));
        return user;
    }

    @Override
    public Optional<User> find(String id) {
        UUID userId = UUID.fromString(id);

        return repository.findById(userId)
                .map(userEntity -> {
                    UserStatsJpaEntity statsEntity = statsRepository.findById(userId)
                            .orElseThrow(() -> new IllegalStateException("UserStats not found for user " + id));

                    return UserMapper.toDomain(userEntity, statsEntity);
                });
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(userEntity -> {
                    UserStatsJpaEntity statsEntity = statsRepository.findById(userEntity.getId())
                            .orElseThrow(() -> new IllegalStateException("UserStats not found for user " + userEntity.getEmail()));

                    return UserMapper.toDomain(userEntity, statsEntity);
                });
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return repository.findByGoogleId(googleId)
                .map(userEntity -> {
                    UserStatsJpaEntity statsEntity = statsRepository.findById(userEntity.getId())
                            .orElseThrow(() -> new IllegalStateException("UserStats not found for user " + userEntity.getGoogleId()));

                    return UserMapper.toDomain(userEntity, statsEntity);
                });
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
