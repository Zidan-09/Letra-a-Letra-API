package com.letraaletra.api.features.items.domain;

import org.springframework.data.domain.Sort;

public record ItemDefinitionsPage(
        int page,
        int size,
        Sort sort
) {
}
