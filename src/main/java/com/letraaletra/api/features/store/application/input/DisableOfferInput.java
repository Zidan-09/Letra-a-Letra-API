package com.letraaletra.api.features.store.application.input;

import java.util.UUID;

public record DisableOfferInput(
        UUID auth,
        UUID offerId
) {
}
