package com.letraaletra.api.features.transaction.domain;

import com.letraaletra.api.features.offers.domain.CoinType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
        UUID transactionId,
        UUID userId,
        CoinType coinType,
        int amount,
        int balanceBefore,
        int balanceAfter,
        OperationType operation,
        TransactionReason reason,
        UUID referenceId,
        LocalDateTime createdAt
) {
    public static Transaction create(
            UUID userId,
            CoinType coinType,
            int amount,
            int balanceBefore,
            int balanceAfter,
            OperationType operation,
            TransactionReason reason,
            UUID referenceId
    ) {
        return new Transaction(
                UUID.randomUUID(),
                userId,
                coinType,
                amount,
                balanceBefore,
                balanceAfter,
                operation,
                reason,
                referenceId,
                LocalDateTime.now()
        );
    }
}
