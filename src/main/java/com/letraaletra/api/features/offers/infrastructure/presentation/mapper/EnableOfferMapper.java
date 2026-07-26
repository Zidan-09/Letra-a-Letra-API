package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.EnableOfferInput;
import com.letraaletra.api.features.offers.application.output.EnableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.EnableOfferResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class EnableOfferMapper {
    public static EnableOfferInput toInput(AuthenticatedUser principal, UUID offerId) {
        return new EnableOfferInput(
                principal,
                offerId
        );
    }

    public static EnableOfferResponse toResponse(EnableOfferOutput output) {
        return new EnableOfferResponse(
                output.offer()
        );
    }
}
