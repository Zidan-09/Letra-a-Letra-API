package com.letraaletra.api.features.store.domain.repository;

import com.letraaletra.api.features.store.domain.StoreOffer;

import java.util.Optional;

public interface FindOfferById {
    Optional<StoreOffer> findById(String offerId);
}
