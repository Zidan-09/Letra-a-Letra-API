package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level.LevelRewardResponse;
import com.letraaletra.api.shared.infrastructure.presentation.mapper.RewardResponseMapper;

public class LevelRewardResponseMapper {
    public static LevelRewardResponse toResponse(LevelReward levelReward) {
        return new LevelRewardResponse(
                levelReward.levelRewardId(),
                RewardResponseMapper.toResponse(levelReward.reward())
        );
    }
}
