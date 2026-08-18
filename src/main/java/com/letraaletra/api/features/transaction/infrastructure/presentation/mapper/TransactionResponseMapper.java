package com.letraaletra.api.features.transaction.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;

public class TransactionResponseMapper {
    public static TransactionResponse toResponse(TransactionDetails transaction) {
        return new TransactionResponse(
                transaction.transactionId(),
                transaction.userId(),
                transaction.username(),
                transaction.coinType(),
                transaction.amount(),
                transaction.balanceBefore(),
                transaction.balanceAfter(),
                transaction.operation(),
                transaction.reason(),
                transaction.referenceId(),
                transaction.referenceType(),
                transaction.referenceName(),
                transaction.transactionDate()
        );
    }
}
