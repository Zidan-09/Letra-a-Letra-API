package com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level;

import com.letraaletra.api.shared.infrastructure.presentation.dto.response.reward.RewardResponse;

import java.util.UUID;

public record LevelRewardResponse(
        UUID levelRewardId,
        RewardResponse reward
) {
}
