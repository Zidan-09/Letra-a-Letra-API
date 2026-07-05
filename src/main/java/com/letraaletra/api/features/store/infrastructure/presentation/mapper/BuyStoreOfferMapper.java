package com.letraaletra.api.features.store.infrastructure.presentation.mapper;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.BuyStoreOfferResponse;
import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public class BuyStoreOfferMapper {
    public static BuyOfferInput toInput(User user, String offerId) {
        return new BuyOfferInput(
               user,
               UUID.fromString(offerId)
        );
    }

    public static BuyStoreOfferResponse toResponse(BuyOfferOutput output) {
        return new BuyStoreOfferResponse(
                output.offer()
        );
    }
}
