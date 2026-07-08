package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;

import java.util.List;

public class OfferMapper {
    public static Offer toDomain(OfferJpaEntity entity, List<OfferReward> rewards) {
        if (entity == null) return null;

        return new Offer(
                entity.getId(),
                entity.getTitle(),
                entity.getCoinType(),
                entity.getPrice(),
                rewards,
                entity.isActive(),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }

    public static OfferJpaEntity toEntity(Offer domain) {
        if (domain == null) return null;

        OfferJpaEntity entity = new OfferJpaEntity();

        entity.setId(domain.getOfferId());
        entity.setTitle(domain.getTitle());
        entity.setCoinType(domain.getCoinType());
        entity.setPrice(domain.getPrice());
        entity.setActive(domain.isActive());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}
