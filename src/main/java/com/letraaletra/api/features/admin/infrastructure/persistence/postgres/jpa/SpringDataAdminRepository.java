package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAdminRepository extends JpaRepository<AdminJpaEntity, UUID> {
    boolean existsByEmail(String email);
    Optional<AdminJpaEntity> findByEmail(String email);
}
