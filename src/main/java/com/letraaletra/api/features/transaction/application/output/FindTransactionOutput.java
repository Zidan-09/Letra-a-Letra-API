package com.letraaletra.api.features.transaction.application.output;

import com.letraaletra.api.features.transaction.domain.Transaction;

public record FindTransactionOutput(
        Transaction transaction
) {
}
