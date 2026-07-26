package com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;

import java.util.List;

public record FindTransactionsByUserResponse(
        List<TransactionResponse> transactions
) {
}
