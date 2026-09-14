package com.letraaletra.api.features.reward.infrastructure.presentation.mapper;

import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.features.reward.infrastructure.presentation.dto.response.RewardResponse;

public class RewardResponseMapper {
    public static RewardResponse toResponse(Reward reward) {
        switch (reward) {
            case ItemGrantReward r -> {
                return new RewardResponse(
                        RewardType.ITEM,
                        r.quantity()
                );
            }
            case HardGemsReward r -> {
                return new RewardResponse(
                        RewardType.GEMS,
                        r.amount()
                );
            }
            case SoftCoinsReward r -> {
                return new RewardResponse(
                        RewardType.COIN,
                        r.amount()
                );
            }
        }
    }
}
