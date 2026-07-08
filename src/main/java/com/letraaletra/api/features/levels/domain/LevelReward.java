package com.letraaletra.api.features.levels.domain;

import com.letraaletra.api.shared.domain.rewards.Reward;

import java.util.UUID;

public record LevelReward(
        UUID levelRewardId,
        Reward reward
) {
}
