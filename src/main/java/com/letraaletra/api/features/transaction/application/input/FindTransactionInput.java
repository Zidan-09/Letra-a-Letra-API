package com.letraaletra.api.features.transaction.application.input;

import java.util.UUID;

public record FindTransactionInput(
        UUID transactionId
) {
}
