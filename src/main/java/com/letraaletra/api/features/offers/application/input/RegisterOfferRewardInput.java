package com.letraaletra.api.features.offers.application.input;

import com.letraaletra.api.features.reward.domain.RewardType;

import java.util.UUID;

public record RegisterOfferRewardInput(
        RewardType rewardType,
        UUID rewardReference,
        int quantity
) {
}