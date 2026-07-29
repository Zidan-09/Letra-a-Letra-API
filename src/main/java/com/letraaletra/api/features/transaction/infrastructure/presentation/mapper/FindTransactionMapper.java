package com.letraaletra.api.features.transaction.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.application.input.FindTransactionInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.FindTransactionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class FindTransactionMapper {
    public static FindTransactionInput toInput(AuthenticatedUser principal, UUID transactionId) {
        return new FindTransactionInput(
                principal,
                transactionId
        );
    }

    public static FindTransactionResponse toResponse(FindTransactionOutput output) {
        return new FindTransactionResponse(
                TransactionResponseMapper
                        .toResponse(output.transaction())
        );
    }
}
