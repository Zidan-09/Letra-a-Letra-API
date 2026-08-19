package com.letraaletra.api.features.levels.domain;

import com.letraaletra.api.features.reward.domain.Reward;

import java.util.UUID;

public record LevelReward(
        UUID levelRewardId,
        Reward reward
) {
}
