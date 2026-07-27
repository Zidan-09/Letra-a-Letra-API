package com.letraaletra.api.features.offers.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.offer.OfferResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

public record GetOffersResponse(
        PageResponse<OfferResponse> offers
) {
}
