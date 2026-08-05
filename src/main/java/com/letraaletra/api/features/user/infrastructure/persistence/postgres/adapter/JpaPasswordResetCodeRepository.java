package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.repository.ResetCodeRepository;
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
    public Optional<PasswordResetCode> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(PasswordResetCodeJpaMapper::toDomain);
    }

    @Override
    public Optional<PasswordResetCode> findLatestByUserId(UUID userId) {
        return repository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .map(PasswordResetCodeJpaMapper::toDomain);
    }

    @Override
    public Optional<PasswordResetCode> findByCodeHash(String codeHash) {
        return repository
                .findValidByCodeHash(codeHash)
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
