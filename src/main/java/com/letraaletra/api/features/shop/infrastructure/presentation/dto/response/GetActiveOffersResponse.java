package com.letraaletra.api.features.shop.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.offers.domain.Offer;

import java.util.List;

public record GetActiveOffersResponse(
        List<Offer> offers
) {
}
