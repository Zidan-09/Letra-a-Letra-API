package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.projection;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.TransactionReason;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionProjection {

    UUID getTransactionId();

    UUID getUserId();

    String getUsername();

    CoinType getCoinType();

    int getAmount();

    int getBalanceBefore();

    int getBalanceAfter();

    OperationType getOperation();

    TransactionReason getReason();

    UUID getReferenceId();

    String getReferenceType();

    String getReferenceName();

    LocalDateTime getTransactionDate();
}
