package com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.TransactionReason;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        UUID userId,
        CoinType coinType,
        int amount,
        int balanceBefore,
        int balanceAfter,
        OperationType operation,
        TransactionReason reason,
        UUID referenceId,
        LocalDateTime transactionDate
) {
}
