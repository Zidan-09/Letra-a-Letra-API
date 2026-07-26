package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.DeleteOfferInput;
import com.letraaletra.api.features.offers.application.output.DeleteOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.DeleteOfferResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DeleteOfferMapper {
    public static DeleteOfferInput toInput(AuthenticatedUser principal, UUID offerId) {
        return new DeleteOfferInput(
                principal,
                offerId
        );
    }

    public static DeleteOfferResponse toResponse(DeleteOfferOutput output) {
        return new DeleteOfferResponse(
                output.offer()
        );
    }
}
