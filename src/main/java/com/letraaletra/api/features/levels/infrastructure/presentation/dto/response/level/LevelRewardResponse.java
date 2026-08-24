package com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level;

import com.letraaletra.api.features.reward.infrastructure.presentation.dto.response.RewardResponse;

import java.util.UUID;

public record LevelRewardResponse(
        UUID levelRewardId,
        RewardResponse reward
) {
}
