package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
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
    public Optional<TransactionDetails> find(UUID id) {
        return repository.findByIdDetails(id)
                .map(TransactionMapper::toDetails);
    }

    @Override
    public Page<TransactionDetails> get(TransactionsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findAllDetails(pageable)
                .map(TransactionMapper::toDetails);
    }

    @Override
    public Page<TransactionDetails> getByUserId(UUID userId, TransactionsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findByUserIdDetails(userId, pageable)
                .map(TransactionMapper::toDetails);
    }

    @Override
    public void save(Transaction transaction) {
        repository.save(TransactionMapper.toEntity(transaction));
    }

    @Override
    public boolean existsOfferPurchase(UUID userId, UUID referenceId) {
        return repository.hasPurchasedOffer(
                userId,
                referenceId,
                TransactionReason.SHOP_PURCHASE
        );
    }
}
