package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataOfferRepository extends JpaRepository<OfferJpaEntity, UUID> {
    List<OfferJpaEntity> findByActive(boolean active);

    @Modifying
    @Query("""
        UPDATE OfferJpaEntity o
        SET o.active = false
        WHERE o.active = true
          AND o.hasExpiration = true
          AND o.expiresAt <= :now
   """)
    void expireOffers(@Param("now") LocalDateTime now);
}
