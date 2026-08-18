package com.letraaletra.api.features.admin.domain;

import org.springframework.data.domain.Sort;

public record AdminsPage(
        int page,
        int size,
        Sort sort
) {
}
