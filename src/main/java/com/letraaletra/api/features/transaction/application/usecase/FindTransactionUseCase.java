package com.letraaletra.api.features.transaction.application.usecase;

import com.letraaletra.api.features.transaction.application.input.FindTransactionInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionOutput;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.exception.TransactionNotFoundException;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindTransactionUseCase implements UseCase<FindTransactionInput, FindTransactionOutput> {
    private final TransactionRepository transactionRepository;

    public FindTransactionUseCase(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public FindTransactionOutput execute(FindTransactionInput input) {
        Transaction transaction = transactionRepository.find(input.transactionId())
                .orElseThrow(TransactionNotFoundException::new);

        return new FindTransactionOutput(transaction);
    }
}
