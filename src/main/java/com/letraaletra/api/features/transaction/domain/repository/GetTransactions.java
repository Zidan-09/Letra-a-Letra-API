package com.letraaletra.api.features.transaction.domain.repository;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GetTransactions {
    Page<TransactionDetails> get(TransactionsPage page);
    Page<TransactionDetails> getByUserId(UUID userId, TransactionsPage page);
}
