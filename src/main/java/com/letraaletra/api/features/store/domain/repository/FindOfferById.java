package com.letraaletra.api.features.store.domain.repository;

import com.letraaletra.api.features.store.domain.StoreOffer;

import java.util.Optional;
import java.util.UUID;

public interface FindOfferById {
    Optional<StoreOffer> findById(UUID offerId);
}
