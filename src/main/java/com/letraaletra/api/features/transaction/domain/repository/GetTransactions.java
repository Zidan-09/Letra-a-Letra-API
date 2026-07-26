package com.letraaletra.api.features.transaction.domain.repository;

import com.letraaletra.api.features.transaction.application.input.GetTransactionsInput;
import com.letraaletra.api.features.transaction.domain.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface GetTransactions {
    Page<Transaction> get(GetTransactionsInput input);
    List<Transaction> getByUserId(UUID userId);
}
