package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.FindOfferInput;
import com.letraaletra.api.features.offers.application.output.FindOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.FindOfferResponse;

import java.util.UUID;

public class FindOfferMapper {
    public static FindOfferInput toInput(UUID offerId) {
        return new FindOfferInput(
                offerId
        );
    }

    public static FindOfferResponse toResponse(FindOfferOutput output) {
        return new FindOfferResponse(
                output.offer()
        );
    }
}
