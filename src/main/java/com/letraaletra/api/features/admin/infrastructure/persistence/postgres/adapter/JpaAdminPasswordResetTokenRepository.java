package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataAdminPasswordResetTokenRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper.AdminPasswordResetTokenJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaAdminPasswordResetTokenRepository implements AdminResetTokenRepository {
    private final SpringDataAdminPasswordResetTokenRepository repository;

    @Override
    public Optional<AdminPasswordResetToken> findById(UUID id) {
        return repository.findById(id)
                .map(AdminPasswordResetTokenJpaMapper::toDomain);
    }

    @Override
    public Optional<AdminPasswordResetToken> findLatestByAdminId(UUID adminId) {
        return repository.findByAdminId(adminId)
                .map(AdminPasswordResetTokenJpaMapper::toDomain);
    }

    @Override
    public Optional<AdminPasswordResetToken> findByTokenHash(String tokenHash) {
        return repository
                .findValidByTokenHash(tokenHash)
                .map(AdminPasswordResetTokenJpaMapper::toDomain);
    }

    @Override
    public void invalidateAllByAdminId(UUID adminId) {
        repository.invalidateAllByAdminId(adminId);
    }

    @Override
    public void save(AdminPasswordResetToken adminPasswordResetToken) {
        repository.save(AdminPasswordResetTokenJpaMapper
                .toEntity(adminPasswordResetToken)
        );
    }
}
