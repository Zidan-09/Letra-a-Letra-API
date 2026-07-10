package com.letraaletra.api.features.offers.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.RewardType;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record RegisterOfferRewardRequest(
        @NotBlank
        RewardType rewardType,

        UUID rewardReference,

        int quantity
) {
}