package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPasswordResetTokenJpaEntity;

public class AdminPasswordResetTokenJpaMapper {
    public static AdminPasswordResetToken toDomain(AdminPasswordResetTokenJpaEntity entity) {
        return AdminPasswordResetToken.restore(
                entity.getId(),
                entity.getAdminId(),
                entity.getTokenHash(),
                entity.isUsed(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }

    public static AdminPasswordResetTokenJpaEntity toEntity(AdminPasswordResetToken domain) {
        AdminPasswordResetTokenJpaEntity entity = new AdminPasswordResetTokenJpaEntity();

        entity.setId(domain.getId());
        entity.setAdminId(domain.getAdminId());
        entity.setTokenHash(domain.getTokenHash());
        entity.setUsed(domain.isUsed());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());

        return entity;
    }
}
