package com.letraaletra.api.shared.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.CosmeticMapper;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.Reward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.reward.RewardResponse;

public class RewardResponseMapper {
    public static RewardResponse toResponse(Reward reward) {
        switch (reward) {
            case CosmeticReward r -> {
                return new RewardResponse(
                        RewardType.COSMETIC,
                        1,
                        CosmeticMapper.toDto(r.cosmetic())
                );
            }
            case HardGemsReward r -> {
                return new RewardResponse(
                        RewardType.GEMS,
                        r.amount(),
                        null
                );
            }
            case SoftCoinsReward r -> {
                return new RewardResponse(
                        RewardType.COIN,
                        r.amount(),
                        null
                );
            }
        }
    }
}
