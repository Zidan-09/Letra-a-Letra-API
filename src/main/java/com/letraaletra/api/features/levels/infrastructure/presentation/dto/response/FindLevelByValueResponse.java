package com.letraaletra.api.features.levels.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level.LevelResponse;

public record FindLevelByValueResponse(
        LevelResponse level
) {
}
