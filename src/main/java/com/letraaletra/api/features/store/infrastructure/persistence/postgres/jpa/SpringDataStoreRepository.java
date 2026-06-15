package com.letraaletra.api.features.store.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.store.infrastructure.persistence.postgres.entity.StoreOfferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataStoreRepository extends JpaRepository<StoreOfferJpaEntity, UUID> {

}
