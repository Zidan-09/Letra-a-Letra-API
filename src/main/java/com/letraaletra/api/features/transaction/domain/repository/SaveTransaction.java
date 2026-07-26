package com.letraaletra.api.features.transaction.domain.repository;

import com.letraaletra.api.features.transaction.domain.Transaction;

public interface SaveTransaction {
    void save(Transaction transaction);
}
