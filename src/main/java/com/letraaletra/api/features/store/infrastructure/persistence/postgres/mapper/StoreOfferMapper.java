package com.letraaletra.api.features.store.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.entity.StoreOfferJpaEntity;

import java.util.UUID;

public class StoreOfferMapper {
    public static StoreOffer toDomain(StoreOfferJpaEntity entity, Cosmetic cosmetic) {
        return new StoreOffer(
                entity.getId().toString(),
                entity.getTitle(),
                entity.getCoinType(),
                entity.getPrice(),
                cosmetic,
                entity.getRewardSoftCoins(),
                entity.getRewardHardGems(),
                entity.isActive(),
                entity.getExpiresAt()
        );
    }

    public static StoreOfferJpaEntity toEntity(StoreOffer domain) {
        StoreOfferJpaEntity entity = new StoreOfferJpaEntity();

        entity.setId(UUID.fromString(domain.getOfferId()));
        entity.setTitle(domain.getTitle());
        entity.setCoinType(domain.getCoinType());
        entity.setPrice(domain.getPrice());
        entity.setCosmeticId(domain.getCosmetic() != null ? domain.getCosmetic().getId() : null);
        entity.setRewardSoftCoins(domain.getRewardSoftCoins());
        entity.setRewardHardGems(domain.getRewardHardGems());
        entity.setActive(domain.isActive());
        entity.setExpiresAt(domain.getExpiresAt());

        return entity;
    }
}
