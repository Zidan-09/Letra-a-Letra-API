package com.letraaletra.api.features.offers.application.output;

import com.letraaletra.api.features.offers.domain.Offer;

import java.util.List;

public record GetOffersOutput(
        List<Offer> offers
) {
}
