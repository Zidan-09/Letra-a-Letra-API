package com.letraaletra.api.features.levels.application.output;

import com.letraaletra.api.features.levels.domain.Level;

import java.util.List;

public record GetLevelsOutput(
        List<Level> levels
) {
}
