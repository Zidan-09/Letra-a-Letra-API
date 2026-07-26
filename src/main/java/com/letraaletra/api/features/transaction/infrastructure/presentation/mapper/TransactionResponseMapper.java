package com.letraaletra.api.features.transaction.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;

public class TransactionResponseMapper {
    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.transactionId(),
                transaction.userId(),
                transaction.coinType(),
                transaction.amount(),
                transaction.balanceBefore(),
                transaction.balanceAfter(),
                transaction.operation(),
                transaction.reason(),
                transaction.referenceId(),
                transaction.createdAt()
        );
    }
}
