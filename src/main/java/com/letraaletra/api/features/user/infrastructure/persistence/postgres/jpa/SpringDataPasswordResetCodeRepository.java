package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.PasswordResetCodeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPasswordResetCodeRepository extends JpaRepository<PasswordResetCodeJpaEntity, UUID> {
    @Modifying
    @Query("""
        UPDATE PasswordResetCodeJpaEntity p
        SET p.used = true
        WHERE p.userId = :userId
        AND p.used = false
    """)
    void invalidateAllByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT p
        FROM PasswordResetCodeJpaEntity p
        WHERE p.codeHash = :codeHash
            AND p.used = false
            AND p.expiresAt > CURRENT_TIMESTAMP
    """)
    Optional<PasswordResetCodeJpaEntity> findValidByCodeHash(@Param("codeHash") String codeHash);
}
