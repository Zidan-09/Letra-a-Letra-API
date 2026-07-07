package com.letraaletra.api.features.shop.infrastructure.presentation.mapper;

import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.shop.infrastructure.presentation.dto.response.BuyStoreOfferResponse;

import java.util.UUID;

public class BuyStoreOfferMapper {
    public static BuyOfferInput toInput(UUID auth, UUID offerId) {
        return new BuyOfferInput(
               auth,
               offerId
        );
    }

    public static BuyStoreOfferResponse toResponse(BuyOfferOutput output) {
        return new BuyStoreOfferResponse(
                output.offer()
        );
    }
}
