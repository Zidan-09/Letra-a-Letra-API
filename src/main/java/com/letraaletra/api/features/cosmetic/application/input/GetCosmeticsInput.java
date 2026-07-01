package com.letraaletra.api.features.cosmetic.application.input;

import org.springframework.data.domain.Sort;

public record GetCosmeticsInput(
        int page,
        int size,
        Sort sort
) {
}
