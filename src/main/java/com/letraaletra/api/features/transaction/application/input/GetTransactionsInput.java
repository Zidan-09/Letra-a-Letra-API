package com.letraaletra.api.features.transaction.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetTransactionsInput(
        UUID auth,
        int page,
        int size,
        Sort sort
) {
}
