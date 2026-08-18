package com.letraaletra.api.features.offers.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record DisableOfferInput(
        AuthenticatedUser principal,
        UUID offerId
) {
}
