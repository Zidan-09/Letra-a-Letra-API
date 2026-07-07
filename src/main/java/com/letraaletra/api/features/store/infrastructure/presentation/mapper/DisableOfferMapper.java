package com.letraaletra.api.features.store.infrastructure.presentation.mapper;

import com.letraaletra.api.features.store.application.input.DisableOfferInput;
import com.letraaletra.api.features.store.application.output.DisableOfferOutput;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.DisableOfferResponse;

import java.util.UUID;

public class DisableOfferMapper {
    public static DisableOfferInput toInput(UUID auth, String offerId) {
        return new DisableOfferInput(
                auth,
                UUID.fromString(offerId)
        );
    }

    public static DisableOfferResponse toResponse(DisableOfferOutput output) {
        return new DisableOfferResponse(
                output.offer()
        );
    }
}
