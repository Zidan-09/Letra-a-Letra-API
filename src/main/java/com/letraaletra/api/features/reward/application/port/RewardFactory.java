package com.letraaletra.api.features.reward.application.port;

import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.Reward;

import java.util.UUID;

public interface RewardFactory {
    Reward create(RewardType type, Integer quantity, UUID referenceId);
}
