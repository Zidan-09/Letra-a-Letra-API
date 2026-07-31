package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminSetupPasswordTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAdminSetupPasswordTokenRepository extends JpaRepository<AdminSetupPasswordTokenJpaEntity, String> {
}
