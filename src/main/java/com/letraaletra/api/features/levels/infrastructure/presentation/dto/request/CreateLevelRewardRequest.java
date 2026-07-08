package com.letraaletra.api.features.levels.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.RewardType;

import java.util.UUID;

public record CreateLevelRewardRequest(
        RewardType rewardType,
        UUID rewardReference,
        int quantity
) {
}
