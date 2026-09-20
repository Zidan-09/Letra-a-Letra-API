package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserSessionJpaEntity;

public class UserSessionJpaMapper {
    public static UserSession toDomain(UserSessionJpaEntity entity) {
        return UserSession.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getCurrentTokenHash(),
                entity.getPreviousTokenHash(),
                entity.getPreviousTokenRotatedAt(),
                entity.getCreatedAt(),
                entity.getLastUsedAt(),
                entity.getExpiresAt(),
                entity.getRevokedAt(),
                entity.getRevocationReason() == null
                        ? null
                        : SessionRevocationReason.valueOf(entity.getRevocationReason())
        );
    }

    public static UserSessionJpaEntity toEntity(UserSession domain) {
        UserSessionJpaEntity entity = new UserSessionJpaEntity();

        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCurrentTokenHash(domain.getCurrentTokenHash());
        entity.setPreviousTokenHash(domain.getPreviousTokenHash());
        entity.setPreviousTokenRotatedAt(domain.getPreviousTokenRotatedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setLastUsedAt(domain.getLastUsedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        entity.setRevocationReason(domain.getRevocationReason() == null
                ? null
                : domain.getRevocationReason().name());

        return entity;
    }
}
