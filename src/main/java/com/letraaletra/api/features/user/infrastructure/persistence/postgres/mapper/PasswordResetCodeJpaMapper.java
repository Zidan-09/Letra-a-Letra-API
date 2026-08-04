package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.PasswordResetCodeJpaEntity;

public class PasswordResetCodeJpaMapper {
    public static PasswordResetCode toDomain(PasswordResetCodeJpaEntity entity) {
        return PasswordResetCode.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getCodeHash(),
                entity.isUsed(),
                entity.getAttempts(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }

    public static PasswordResetCodeJpaEntity toEntity(PasswordResetCode domain) {
        PasswordResetCodeJpaEntity entity = new PasswordResetCodeJpaEntity();

        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCodeHash(domain.getCodeHash());
        entity.setUsed(domain.isUsed());
        entity.setAttempts(domain.getAttempts());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());

        return entity;
    }
}
