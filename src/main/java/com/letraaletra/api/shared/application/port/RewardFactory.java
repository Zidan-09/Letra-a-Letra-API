package com.letraaletra.api.shared.application.port;

import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.shared.domain.rewards.Reward;

import java.util.UUID;

public interface RewardFactory {
    Reward create(RewardType type, Integer quantity, UUID referenceId);
}
