package com.letraaletra.api.features.transaction.application.output;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import org.springframework.data.domain.Page;

public record GetTransactionsOutput(
        Page<TransactionDetails> transactions
) {
}
