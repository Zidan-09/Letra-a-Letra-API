package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.transaction.application.input.GetTransactionsInput;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.jpa.SpringDataTransactionRepository;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.mapper.TransactionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final SpringDataTransactionRepository repository;

    public JpaTransactionRepository(
            SpringDataTransactionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<Transaction> find(UUID id) {
        return repository.findById(id)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public Page<Transaction> get(GetTransactionsInput input) {
        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        return repository.findAll(pageable)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> getByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Transaction transaction) {
        repository.save(TransactionMapper.toEntity(transaction));
    }
}
