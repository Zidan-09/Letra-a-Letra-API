package com.letraaletra.api.features.store.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.store.domain.offer.Offer;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.entity.StoreOfferJpaEntity;

import java.util.UUID;

public class StoreOfferMapper {
    public static Offer toDomain(StoreOfferJpaEntity entity, Cosmetic cosmetic) {
        return new Offer(
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

    public static StoreOfferJpaEntity toEntity(Offer domain) {
        StoreOfferJpaEntity entity = new StoreOfferJpaEntity();

        entity.setId(UUID.fromString(domain.offer_id()));
        entity.setTitle(domain.title());
        entity.setCoinType(domain.coinType());
        entity.setPrice(domain.price());
        entity.setCosmeticId(domain.cosmetic() != null ? UUID.fromString(domain.cosmetic().id()) : null);
        entity.setRewardSoftCoins(domain.rewardSoftCoins());
        entity.setRewardHardGems(domain.rewardHardGems());
        entity.setActive(domain.active());
        entity.setExpiresAt(domain.expiresAt());

        return entity;
    }
}
