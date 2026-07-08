package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferRewardInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.request.RegisterOfferRewardRequest;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.RegisterOfferResponse;

import java.util.UUID;

public class RegisterOfferMapper {
    public static RegisterOfferInput toInput(UUID auth, RegisterOfferRequest request) {
        return new RegisterOfferInput(
                auth,
                request.title(),
                request.coinType(),
                request.price(),
                request.rewards().stream()
                        .map(RegisterOfferMapper::toRewardInput)
                        .toList(),
                request.expiresIn()
        );
    }

    public static RegisterOfferResponse toResponse(RegisterOfferOutput output) {
        return new RegisterOfferResponse(
                output.offer()
        );
    }

    private static RegisterOfferRewardInput toRewardInput(RegisterOfferRewardRequest reward) {
        return new RegisterOfferRewardInput(
                reward.rewardType(),
                reward.rewardReference(),
                reward.quantity()
        );
    }
}
