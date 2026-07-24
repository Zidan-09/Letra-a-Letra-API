package com.letraaletra.api.features.offers.domain.repository;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.domain.Offer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GetOffers {
    Page<Offer> get(GetOffersInput input);
    List<Offer> getActiveOffers();
}
