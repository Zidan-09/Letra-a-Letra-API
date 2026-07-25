package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.WalletTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataUserWalletTransactionRepository extends JpaRepository<WalletTransactionJpaEntity, UUID> {
    List<WalletTransactionJpaEntity> findByUserId(UUID userId);
}
