package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserSessionRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserSessionJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserSessionRepository implements UserSessionRepository {
    private final SpringDataUserSessionRepository repository;

    @Override
    public Optional<UserSession> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(UserSessionJpaMapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByUserIdForUpdate(UUID userId) {
        return repository.findByUserIdForUpdate(userId)
                .map(UserSessionJpaMapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByTokenHashForUpdate(String tokenHash) {
        return repository.findByTokenHashForUpdate(tokenHash)
                .map(UserSessionJpaMapper::toDomain);
    }

    @Override
    public void save(UserSession session) {
        repository.save(UserSessionJpaMapper.toEntity(session));
    }
}
