package com.letraaletra.api.features.offers.domain.repository;

import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OffersPage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GetOffers {
    Page<Offer> get(OffersPage page);
    List<Offer> getActiveOffers();
}
