package com.letraaletra.api.features.cosmetic.domain;

import org.springframework.data.domain.Sort;

public record CosmeticsPage(
        int page,
        int size,
        Sort sort
) {
}
