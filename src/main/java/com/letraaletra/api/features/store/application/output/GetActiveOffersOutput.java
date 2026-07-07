package com.letraaletra.api.features.store.application.output;

import com.letraaletra.api.features.offers.domain.Offer;

import java.util.List;

public record GetActiveOffersOutput(
        List<Offer> offers
) {
}
