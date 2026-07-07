package com.letraaletra.api.features.offers.application.input;

import java.util.UUID;

public record EnableOfferInput(
        UUID auth,
        UUID offerId
) {
}
