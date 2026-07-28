package com.letraaletra.api.features.transaction.application.output;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;

public record FindTransactionOutput(
        TransactionDetails transaction
) {
}
