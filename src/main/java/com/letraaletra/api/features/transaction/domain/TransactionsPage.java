package com.letraaletra.api.features.transaction.domain;

import org.springframework.data.domain.Sort;

public record TransactionsPage(
        int page,
        int size,
        Sort sort
) {
}
