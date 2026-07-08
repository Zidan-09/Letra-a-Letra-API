package com.letraaletra.api.features.levels.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.levels.domain.Level;

import java.util.List;

public record GetLevelsResponse(
        List<Level> levels
) {
}
