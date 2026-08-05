package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAdminPasswordResetTokenRepository extends JpaRepository<AdminPasswordResetTokenJpaEntity, UUID> {
    Optional<AdminPasswordResetTokenJpaEntity> findByAdminId(UUID adminId);

    @Modifying
    @Query("""
        UPDATE AdminPasswordResetTokenJpaEntity p
        SET p.used = true
        WHERE p.adminId = :adminId
        AND p.used = false
    """)
    void invalidateAllByAdminId(@Param("adminId") UUID adminId);
}
