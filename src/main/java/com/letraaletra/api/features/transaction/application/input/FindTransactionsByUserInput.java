package com.letraaletra.api.features.transaction.application.input;

import java.util.UUID;

public record FindTransactionsByUserInput(
        UUID auth,
        UUID userId
) {
}
