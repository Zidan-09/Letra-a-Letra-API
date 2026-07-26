package com.letraaletra.api.features.transaction.application.output;

import com.letraaletra.api.features.transaction.domain.Transaction;

import java.util.List;

public record FindTransactionsByUserOutput(
        List<Transaction> transactions
) {
}
