package com.letraaletra.api.features.levels.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.levels.domain.Level;

public record FindLevelResponse(
        Level level
) {
}
