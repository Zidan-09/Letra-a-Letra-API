package com.letraaletra.api.features.store.infrastructure.presentation.mapper;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.BuyStoreOfferResponse;

import java.util.UUID;

public class BuyStoreOfferMapper {
    public static BuyOfferInput toInput(String userId, String offerId) {
        return new BuyOfferInput(
               UUID.fromString(userId),
               offerId
        );
    }

    public static BuyStoreOfferResponse toResponse(BuyOfferOutput output) {
        return new BuyStoreOfferResponse(
                output.offer()
        );
    }
}
