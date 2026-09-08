package com.letraaletra.api.features.transaction.domain;

import com.letraaletra.api.features.offers.domain.CoinType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDetails(
        UUID transactionId,
        UUID userId,
        String username,

        CoinType coinType,
        long amount,
        long balanceBefore,
        long balanceAfter,

        OperationType operation,

        TransactionReason reason,

        UUID referenceId,
        String referenceType,
        String referenceName,

        LocalDateTime transactionDate
) {
}
