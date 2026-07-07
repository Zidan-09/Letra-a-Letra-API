package com.letraaletra.api.features.offers.domain.repository;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.domain.Offer;

import java.util.List;

public interface GetOffers {
    List<Offer> get(GetOffersInput input);
    List<Offer> getActiveOffers();
}
