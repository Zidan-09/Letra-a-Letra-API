package com.letraaletra.api.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository repository;

    public JpaUserRepository(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(User user) {
        repository.save(UserMapper.toEntity(user));
    }

    @Override
    public User find(String id) {
        return repository.findById(UUID.fromString(id))
                .map(UserMapper::toDomain)
                .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return UserMapper.toDomain(repository.findByEmail(email).orElse(null));
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
