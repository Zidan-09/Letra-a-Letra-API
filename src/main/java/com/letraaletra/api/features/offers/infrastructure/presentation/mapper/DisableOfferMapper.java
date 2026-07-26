package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.DisableOfferInput;
import com.letraaletra.api.features.offers.application.output.DisableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.DisableOfferResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DisableOfferMapper {
    public static DisableOfferInput toInput(AuthenticatedUser principal, UUID offerId) {
        return new DisableOfferInput(
                principal,
                offerId
        );
    }

    public static DisableOfferResponse toResponse(DisableOfferOutput output) {
        return new DisableOfferResponse(
                output.offer()
        );
    }
}
