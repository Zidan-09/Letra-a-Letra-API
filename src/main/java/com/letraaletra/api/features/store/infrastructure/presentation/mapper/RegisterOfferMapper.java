package com.letraaletra.api.features.store.infrastructure.presentation.mapper;

import com.letraaletra.api.features.store.application.input.RegisterOfferInput;
import com.letraaletra.api.features.store.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.RegisterOfferResponse;

import java.util.UUID;

public class RegisterOfferMapper {
    public static RegisterOfferInput toInput(UUID auth, RegisterOfferRequest request) {
        return new RegisterOfferInput(
                auth,
                request.title(),
                request.coinType(),
                request.price(),
                UUID.fromString(request.cosmeticId()),
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
