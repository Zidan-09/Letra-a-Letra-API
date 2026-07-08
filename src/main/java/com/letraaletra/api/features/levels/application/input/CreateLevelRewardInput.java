package com.letraaletra.api.features.levels.application.input;

import com.letraaletra.api.features.offers.domain.RewardType;

import java.util.UUID;

public record CreateLevelRewardInput(
        RewardType rewardType,
        UUID rewardReference,
        int quantity
) {
}
