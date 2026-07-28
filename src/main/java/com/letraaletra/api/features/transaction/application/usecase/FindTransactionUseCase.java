package com.letraaletra.api.features.transaction.application.usecase;

import com.letraaletra.api.features.transaction.application.input.FindTransactionInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionOutput;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.exception.TransactionNotFoundException;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindTransactionUseCase implements UseCase<FindTransactionInput, FindTransactionOutput> {
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;

    public FindTransactionUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public FindTransactionOutput execute(FindTransactionInput input) {
        adminChecker.check(input.principal());

        TransactionDetails transaction = transactionRepository.find(input.transactionId())
                .orElseThrow(TransactionNotFoundException::new);

        return new FindTransactionOutput(transaction);
    }
}
