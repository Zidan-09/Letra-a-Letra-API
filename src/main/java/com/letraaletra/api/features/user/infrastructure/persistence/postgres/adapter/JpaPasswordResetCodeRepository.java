package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.reset.PasswordResetCode;
import com.letraaletra.api.features.user.domain.reset.repository.ResetCodeRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataPasswordResetCodeRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.PasswordResetCodeJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaPasswordResetCodeRepository implements ResetCodeRepository {
    private final SpringDataPasswordResetCodeRepository repository;

    @Override
    public Optional<PasswordResetCode> findById(UUID id) {
        return repository.findById(id)
                .map(PasswordResetCodeJpaMapper::toDomain);
    }

    @Override
    public Optional<PasswordResetCode> findActiveByUserId(UUID userId) {
        return repository
                .findActiveByUserId(userId)
                .stream()
                .findFirst()
                .map(PasswordResetCodeJpaMapper::toDomain);
    }

    @Override
    public void save(PasswordResetCode passwordResetCode) {
        repository.save(PasswordResetCodeJpaMapper.toEntity(passwordResetCode));
    }

    @Override
    public void invalidateAllByUserId(UUID userId) {
        repository.invalidateAllByUserId(userId);
    }
}
