package com.letraaletra.api.features.transaction.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetTransactionsInput(
        AuthenticatedUser principal,
        int page,
        int size,
        Sort sort
) {
}
