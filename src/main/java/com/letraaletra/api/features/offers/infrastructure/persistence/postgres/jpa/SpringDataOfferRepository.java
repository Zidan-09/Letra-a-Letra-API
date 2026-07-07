package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataOfferRepository extends JpaRepository<OfferJpaEntity, UUID> {
    List<OfferJpaEntity> findByActive(boolean active);
}
