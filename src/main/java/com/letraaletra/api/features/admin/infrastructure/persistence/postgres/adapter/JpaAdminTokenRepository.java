package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataAdminSetupPasswordTokenRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper.AdminSetupJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAdminTokenRepository implements AdminTokenRepository {
    private final SpringDataAdminSetupPasswordTokenRepository repository;

    public JpaAdminTokenRepository(
            SpringDataAdminSetupPasswordTokenRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void save(AdminPasswordSetupToken adminPasswordSetupToken) {
        repository.save(AdminSetupJpaMapper.toEntity(adminPasswordSetupToken));
    }

    @Override
    public Optional<AdminPasswordSetupToken> findByTokenHash(String tokenHash) {
        return repository.findById(tokenHash)
                .map(AdminSetupJpaMapper::toDomain);
    }
}
