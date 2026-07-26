package com.letraaletra.api.features.levels.domain;

import org.springframework.data.domain.Sort;

public record LevelsPage(
        int page,
        int size,
        Sort sort
) {
}
