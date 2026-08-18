package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.BanHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataBanHistoryRepository extends JpaRepository<BanHistoryJpaEntity, UUID> {
    @Query("""
    SELECT b
    FROM BanHistoryJpaEntity b
    WHERE b.userId = :userId
      AND b.removedAt IS NULL
      AND (b.expiresAt IS NULL OR b.expiresAt > CURRENT_TIMESTAMP)
    """)
    Optional<BanHistoryJpaEntity> findActiveByUserId(UUID userId);
}
