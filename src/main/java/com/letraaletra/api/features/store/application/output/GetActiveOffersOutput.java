package com.letraaletra.api.features.store.application.output;

import com.letraaletra.api.features.store.domain.StoreOffer;

import java.util.List;

public record GetActiveOffersOutput(
        List<StoreOffer> offers
) {
}
