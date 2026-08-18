package com.letraaletra.api.features.cosmetic.application.input;

import org.springframework.data.domain.Sort;

public record SearchCosmeticInput(
        String search,
        int page,
        int size,
        Sort sort
) {
}
