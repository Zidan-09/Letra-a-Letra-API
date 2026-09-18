package com.letraaletra.api.features.items.domain.catalog;

import org.springframework.data.domain.Sort;

public record ItemsPage(
        int page,
        int size,
        Sort sort
) {
}
