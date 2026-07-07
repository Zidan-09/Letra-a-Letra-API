package com.letraaletra.api.features.shop.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.offers.domain.Offer;

public record BuyStoreOfferResponse(
    Offer offer
) {
}
