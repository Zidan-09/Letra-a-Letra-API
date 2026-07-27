package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level.LevelResponse;

public class LevelResponseMapper {
    public static LevelResponse toResponse(Level level) {
        return new LevelResponse(
            level.getLevelId(),
            level.getLevel(),
            level.getRewards().stream()
                    .map(LevelRewardResponseMapper::toResponse)
                    .toList()
        );
    }
}
