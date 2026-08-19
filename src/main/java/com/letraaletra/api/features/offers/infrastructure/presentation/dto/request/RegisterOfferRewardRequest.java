package com.letraaletra.api.features.offers.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.reward.domain.RewardType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterOfferRewardRequest(
        @NotNull
        RewardType rewardType,

        UUID rewardReference,

        Integer quantity
) {
}