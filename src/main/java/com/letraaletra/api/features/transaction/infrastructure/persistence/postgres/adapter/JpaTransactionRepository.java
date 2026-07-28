package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.jpa.SpringDataTransactionRepository;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.mapper.TransactionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public Page<Transaction> get(TransactionsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findAll(pageable)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public Page<Transaction> getByUserId(UUID userId, TransactionsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findByUserId(userId, pageable)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public void save(Transaction transaction) {
        repository.save(TransactionMapper.toEntity(transaction));
    }
}
