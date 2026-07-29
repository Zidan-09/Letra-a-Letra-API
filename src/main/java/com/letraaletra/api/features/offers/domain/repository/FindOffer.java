package com.letraaletra.api.features.offers.domain.repository;

import com.letraaletra.api.features.offers.domain.Offer;

import java.util.Optional;
import java.util.UUID;

public interface FindOffer {
    Optional<Offer> findById(UUID offerId);
}
