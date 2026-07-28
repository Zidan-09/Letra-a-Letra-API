package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.AdminTransactionsPage;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.transaction.AdminTransactionResponse;
import org.springframework.data.domain.Page;

public interface AdminTransactionRepository {
    Page<AdminTransactionResponse> findAll(AdminTransactionsPage input);
}
