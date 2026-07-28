package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.admin.domain.AdminTransactionsPage;
import com.letraaletra.api.features.admin.domain.repository.AdminTransactionRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataAdminTransactionRepository;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.transaction.AdminTransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.mapper.AdminTransactionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAdminTransactionsRepository implements AdminTransactionRepository {
    private final SpringDataAdminTransactionRepository repository;

    public JpaAdminTransactionsRepository(
        SpringDataAdminTransactionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Page<AdminTransactionResponse> findAll(AdminTransactionsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findAllAdmin(pageable)
                .map(AdminTransactionMapper::toResponse);
    }
}
