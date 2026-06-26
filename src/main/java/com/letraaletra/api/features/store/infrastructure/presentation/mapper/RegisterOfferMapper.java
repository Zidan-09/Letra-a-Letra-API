package com.letraaletra.api.features.store.infrastructure.presentation.mapper;

import com.letraaletra.api.features.store.application.input.RegisterOfferInput;
import com.letraaletra.api.features.store.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.RegisterOfferResponse;

public class RegisterOfferMapper {
    public static RegisterOfferInput toInput(RegisterOfferRequest request) {
        return new RegisterOfferInput(
                request.title(),
                request.coinType(),
                request.price(),
                request.cosmeticId(),
                request.rewardSoftCoins(),
                request.rewardHardGems(),
                request.expiresIn()
        );
    }

    public static RegisterOfferResponse toResponse(RegisterOfferOutput output) {
        return new RegisterOfferResponse(
                output.storeOffer()
        );
    }
}
