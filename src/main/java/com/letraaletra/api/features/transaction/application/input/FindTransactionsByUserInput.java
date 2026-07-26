package com.letraaletra.api.features.transaction.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record FindTransactionsByUserInput(
        AuthenticatedUser principal,
        UUID userId
) {
}
