package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.EnableOfferInput;
import com.letraaletra.api.features.offers.application.output.EnableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.EnableOfferResponse;

import java.util.UUID;

public class EnableOfferMapper {
    public static EnableOfferInput toInput(UUID auth, UUID offerId) {
        return new EnableOfferInput(
                auth,
                offerId
        );
    }

    public static EnableOfferResponse toResponse(EnableOfferOutput output) {
        return new EnableOfferResponse(
                output.offer()
        );
    }
}
