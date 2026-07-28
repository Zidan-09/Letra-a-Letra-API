package com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.entity.TransactionJpaEntity;
import com.letraaletra.api.features.transaction.infrastructure.persistence.postgres.projection.TransactionProjection;

public class TransactionMapper {
    public static Transaction toDomain(TransactionJpaEntity entity) {
        return new Transaction(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getCoinType(),
                entity.getAmount(),
                entity.getBalanceBefore(),
                entity.getBalanceAfter(),
                entity.getOperation(),
                entity.getReason(),
                entity.getReferenceId(),
                entity.getCreatedAt()
        );
    }

    public static TransactionJpaEntity toEntity(Transaction domain) {
        TransactionJpaEntity entity = new TransactionJpaEntity();

        entity.setTransactionId(domain.transactionId());
        entity.setUserId(domain.userId());
        entity.setCoinType(domain.coinType());
        entity.setAmount(domain.amount());
        entity.setBalanceBefore(domain.balanceBefore());
        entity.setBalanceAfter(domain.balanceAfter());
        entity.setOperation(domain.operation());
        entity.setReason(domain.reason());
        entity.setReferenceId(domain.referenceId());
        entity.setCreatedAt(domain.createdAt());

        return entity;
    }

    public static TransactionDetails toDetails(TransactionProjection projection) {
        return new TransactionDetails(
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
