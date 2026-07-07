package com.letraaletra.api.features.offers.application.input;

import java.util.UUID;

public record DeleteOfferInput(
        UUID auth,
        UUID offerId
) {
}
