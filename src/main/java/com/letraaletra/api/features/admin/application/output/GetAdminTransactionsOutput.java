package com.letraaletra.api.features.admin.application.output;

import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.transaction.AdminTransactionResponse;
import org.springframework.data.domain.Page;

public record GetAdminTransactionsOutput(
        Page<AdminTransactionResponse> adminTransactionResponses
) {
}
