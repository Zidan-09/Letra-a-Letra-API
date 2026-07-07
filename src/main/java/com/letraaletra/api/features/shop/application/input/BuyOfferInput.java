package com.letraaletra.api.features.shop.application.input;

import java.util.UUID;

public record BuyOfferInput(
        UUID auth,
        UUID offerId
) {
}
