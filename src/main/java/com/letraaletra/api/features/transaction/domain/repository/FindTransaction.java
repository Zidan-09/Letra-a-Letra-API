package com.letraaletra.api.features.transaction.domain.repository;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;

import java.util.Optional;
import java.util.UUID;

public interface FindTransaction {
    Optional<TransactionDetails> find(UUID id);
}
