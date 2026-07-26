package com.letraaletra.api.features.transaction.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.FindTransactionsByUserResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class FindTransactionsByUserMapper {
    public static FindTransactionsByUserInput toInput(AuthenticatedUser principal, UUID userId) {
        return new FindTransactionsByUserInput(
                principal,
                userId
        );
    }

    public static FindTransactionsByUserResponse toResponse(FindTransactionsByUserOutput output) {
        return new FindTransactionsByUserResponse(
                output.transactions().stream()
                        .map(TransactionResponseMapper::toResponse)
                        .toList()
        );
    }
}
