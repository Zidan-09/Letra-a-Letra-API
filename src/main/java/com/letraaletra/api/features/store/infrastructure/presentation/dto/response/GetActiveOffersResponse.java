package com.letraaletra.api.features.store.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.store.domain.StoreOffer;

import java.util.List;

public record GetActiveOffersResponse(
        List<StoreOffer> activeOffers
) {
}
