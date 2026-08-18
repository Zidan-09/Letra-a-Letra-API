package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.RewardType;

import java.util.UUID;

public record GrantUserRewardRequest(
        RewardType rewardType,
        int quantity,
        UUID rewardReference
) {
}
