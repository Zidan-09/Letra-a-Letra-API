package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserWalletJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUserWalletRepository extends JpaRepository<UserWalletJpaEntity, UUID> {
    UserWalletJpaEntity findByUserId(UUID userId);
}
