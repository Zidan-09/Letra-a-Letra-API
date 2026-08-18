package com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;

public record FindTransactionResponse(
    TransactionResponse transaction
) {
}
