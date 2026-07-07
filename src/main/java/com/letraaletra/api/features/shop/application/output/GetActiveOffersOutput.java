package com.letraaletra.api.features.shop.application.output;

import com.letraaletra.api.features.offers.domain.Offer;

import java.util.List;

public record GetActiveOffersOutput(
        List<Offer> offers
) {
}
