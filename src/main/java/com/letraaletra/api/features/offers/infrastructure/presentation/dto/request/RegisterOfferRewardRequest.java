package com.letraaletra.api.features.offers.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.RewardType;

import java.util.UUID;

public record RegisterOfferRewardRequest(
        RewardType rewardType,
        UUID rewardReference,
        int quantity
) {
}