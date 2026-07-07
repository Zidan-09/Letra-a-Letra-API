package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;

public class OfferMapper {
    public static Offer toDomain(OfferJpaEntity entity, Cosmetic cosmetic) {
        if (entity == null) return null;

        return new Offer(
                entity.getId(),
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

    public static OfferJpaEntity toEntity(Offer domain) {
        if (domain == null) return null;

        OfferJpaEntity entity = new OfferJpaEntity();

        entity.setId(domain.getOfferId());
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
