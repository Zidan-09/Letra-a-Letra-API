package com.letraaletra.api.features.store.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.store.domain.StoreOffer;

public record DisableOfferResponse(
        StoreOffer offer
) {
}
