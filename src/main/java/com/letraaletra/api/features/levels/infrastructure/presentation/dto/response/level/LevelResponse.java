package com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level;

import java.util.List;
import java.util.UUID;

public record LevelResponse(
        UUID levelId,
        Integer value,
        List<LevelRewardResponse> rewards
) {
}
