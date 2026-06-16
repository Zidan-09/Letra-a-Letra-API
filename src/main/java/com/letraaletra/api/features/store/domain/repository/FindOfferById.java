package com.letraaletra.api.features.store.domain.repository;

import com.letraaletra.api.features.store.domain.StoreOffer;

public interface FindOfferById {
    StoreOffer findById(String offerId);
}
