package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record GrantUserRewardInput(
        AuthenticatedUser principal,
        UUID userId,
        RewardType rewardType,
        UUID cosmeticId,
        Integer amount
) {
}
