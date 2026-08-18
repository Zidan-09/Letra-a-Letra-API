package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAdminPasswordResetTokenRepository extends JpaRepository<AdminPasswordResetTokenJpaEntity, UUID> {
    @Modifying
    @Query("""
        UPDATE AdminPasswordResetTokenJpaEntity p
        SET p.used = true
        WHERE p.adminId = :adminId
        AND p.used = false
    """)
    void invalidateAllByAdminId(@Param("adminId") UUID adminId);

    @Query("""
        SELECT p
        FROM AdminPasswordResetTokenJpaEntity p
        WHERE p.tokenHash = :tokenHash
          AND p.used = false
          AND p.expiresAt > CURRENT_TIMESTAMP
    """)
    Optional<AdminPasswordResetTokenJpaEntity> findValidByTokenHash(@Param("tokenHash") String tokenHash);
}
