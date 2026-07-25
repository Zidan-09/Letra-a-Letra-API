package com.letraaletra.api.features.levels.application.output;

import com.letraaletra.api.features.levels.domain.Level;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetLevelsOutput(
        Page<Level> levels
) {
}
