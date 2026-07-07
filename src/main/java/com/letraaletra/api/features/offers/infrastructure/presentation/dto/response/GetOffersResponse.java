package com.letraaletra.api.features.offers.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.offers.domain.Offer;

import java.util.List;

public record GetOffersResponse(
        List<Offer> offers
) {
}
