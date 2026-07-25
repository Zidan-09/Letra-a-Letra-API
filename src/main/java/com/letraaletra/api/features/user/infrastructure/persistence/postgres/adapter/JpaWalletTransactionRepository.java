package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.repository.WalletTransactionRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserWalletTransactionRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.WalletTransactionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWalletTransactionRepository implements WalletTransactionRepository {
    private final SpringDataUserWalletTransactionRepository repository;

    public JpaWalletTransactionRepository(
            SpringDataUserWalletTransactionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<WalletTransaction> find(UUID id) {
        return repository.findById(id)
                .map(WalletTransactionMapper::toDomain);
    }

    @Override
    public List<WalletTransaction> get(Object object) {
        return repository.findAll().stream()
                .map(WalletTransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<WalletTransaction> getByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(WalletTransactionMapper::toDomain)
                .toList();
    }

    @Override
    public void save(WalletTransaction walletTransaction) {
        repository.save(WalletTransactionMapper.toEntity(walletTransaction));
    }
}
