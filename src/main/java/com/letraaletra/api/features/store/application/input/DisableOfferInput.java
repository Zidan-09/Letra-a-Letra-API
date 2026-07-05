package com.letraaletra.api.features.store.application.input;

import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public record DisableOfferInput(
        User user,
        UUID offerId
) {
}
