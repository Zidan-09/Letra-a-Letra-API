package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.ban.BanHistory;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.BanHistoryJpaEntity;

public class BanHistoryJpaMapper {
    public static BanHistory toDomain(BanHistoryJpaEntity entity) {
        return BanHistory.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getAdminId(),
                entity.getReason(),
                entity.getType(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getRemovedAt(),
                entity.getRemovedBy()
        );
    }

    public static BanHistoryJpaEntity toEntity(BanHistory domain) {
        BanHistoryJpaEntity entity = new BanHistoryJpaEntity();

        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setAdminId(domain.getAdminId());
        entity.setReason(domain.getReason());
        entity.setType(domain.getType());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRemovedAt(domain.getRemovedAt());
        entity.setRemovedBy(domain.getRemovedBy());

        return entity;
    }
}
