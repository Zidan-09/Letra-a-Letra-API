package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import org.springframework.data.domain.Page;

public record GetMyTransactionsOutput(
        Page<TransactionDetails> transactions
) {
}
