package com.letraaletra.api.features.offers.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.offers.domain.Offer;

public record DisableOfferResponse(
        Offer offer
) {
}
