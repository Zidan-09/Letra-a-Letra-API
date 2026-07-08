package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferRewardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataOfferRewardRepository extends JpaRepository<OfferRewardJpaEntity, UUID> {
    List<OfferRewardJpaEntity> findByOfferId(UUID offerId);
    void deleteByOfferId(UUID offerId);
}
