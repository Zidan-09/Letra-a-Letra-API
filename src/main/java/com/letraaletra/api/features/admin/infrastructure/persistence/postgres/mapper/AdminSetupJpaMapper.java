package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminSetupPasswordTokenJpaEntity;

public class AdminSetupJpaMapper {
    public static AdminPasswordSetupToken toDomain(AdminSetupPasswordTokenJpaEntity entity) {
        return AdminPasswordSetupToken.restore(
                entity.getTokenHash(),
                entity.getAdminId(),
                entity.getExpiresAt(),
                entity.isUsed()
        );
    }

    public static AdminSetupPasswordTokenJpaEntity toEntity(AdminPasswordSetupToken domain) {
        AdminSetupPasswordTokenJpaEntity entity = new AdminSetupPasswordTokenJpaEntity();

        entity.setAdminId(domain.getAdminId());
        entity.setTokenHash(domain.getTokenHash());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setUsed(domain.isUsed());

        return entity;
    }
}
