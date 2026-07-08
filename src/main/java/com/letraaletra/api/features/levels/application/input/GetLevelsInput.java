package com.letraaletra.api.features.levels.application.input;

import org.springframework.data.domain.Sort;

public record GetLevelsInput(
        int page,
        int size,
        Sort sort
) {
}
