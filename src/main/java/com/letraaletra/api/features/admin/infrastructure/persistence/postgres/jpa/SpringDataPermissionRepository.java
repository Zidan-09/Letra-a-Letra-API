package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPermissionId;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataPermissionRepository extends JpaRepository<AdminPermissionJpaEntity, AdminPermissionId> {
    List<AdminPermissionJpaEntity> findByIdAdminId(UUID adminId);
    List<AdminPermissionJpaEntity> findByIdAdminIdIn(List<UUID> adminIds);
    void deleteByIdAdminId(UUID adminId);
}
