package com.letraaletra.api.features.transaction.domain.repository;

import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface GetTransactions {
    Page<Transaction> get(TransactionsPage page);
    Page<Transaction> getByUserId(UUID userId, TransactionsPage page);
}
