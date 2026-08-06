package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.RewardType;

import java.util.UUID;

public record GrantUserRewardRequest(
        RewardType type,
        int amount,
        UUID reference
) {
}
