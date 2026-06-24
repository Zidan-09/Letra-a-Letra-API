package com.letraaletra.api.features.store.application.input;

import java.util.UUID;

public record BuyOfferInput(
        UUID userId,
        String offerId
) {
}
