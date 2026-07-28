package com.letraaletra.api.features.user.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetMyTransactionsInput(
        UUID id,
        int page,
        int size,
        Sort sort
) {
}
