package com.letraaletra.api.shared.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.CosmeticResponseMapper;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.reward.RewardResponse;

public class RewardResponseMapper {
    public static RewardResponse toResponse(Reward reward) {
        switch (reward) {
            case CosmeticReward r -> {
                return new RewardResponse(
                        RewardType.COSMETIC,
                        1,
                        CosmeticResponseMapper.toResponse(r.cosmetic())
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
