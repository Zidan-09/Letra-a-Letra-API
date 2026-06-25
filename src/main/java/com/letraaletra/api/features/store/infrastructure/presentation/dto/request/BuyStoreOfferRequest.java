package com.letraaletra.api.features.store.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BuyStoreOfferRequest(
        @NotBlank
        String offerId
) {
}
