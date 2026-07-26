package com.letraaletra.api.features.transaction.domain.repository;

import com.letraaletra.api.features.transaction.domain.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface FindTransaction {
    Optional<Transaction> find(UUID id);
}
