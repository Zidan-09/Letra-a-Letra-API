package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserWalletJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUserWalletRepository extends JpaRepository<UserWalletJpaEntity, UUID> {
    <S extends UserStatsJpaEntity> S save(S entity);

    UserWalletJpaEntity findByUserId(UUID userId);
}
