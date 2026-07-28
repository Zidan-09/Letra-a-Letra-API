package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.transaction.AdminTransactionResponse;
import com.letraaletra.api.features.admin.infrastructure.projection.AdminTransactionProjection;

public class AdminTransactionMapper {
    public static AdminTransactionResponse toResponse(AdminTransactionProjection projection) {
        return new AdminTransactionResponse(
                projection.getTransactionId(),
                projection.getUserId(),
                projection.getUsername(),
                projection.getCoinType(),
                projection.getAmount(),
                projection.getBalanceBefore(),
                projection.getBalanceAfter(),
                projection.getOperation(),
                projection.getReason(),
                projection.getReferenceId(),
                projection.getReferenceType(),
                projection.getReferenceName(),
                projection.getTransactionDate()
        );
    }
}
