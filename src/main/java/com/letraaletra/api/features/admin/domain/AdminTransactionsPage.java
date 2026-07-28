package com.letraaletra.api.features.admin.domain;

import org.springframework.data.domain.Sort;

public record AdminTransactionsPage(
        int page,
        int size,
        Sort sort
) {
}
