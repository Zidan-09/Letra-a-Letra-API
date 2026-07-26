package com.letraaletra.api.features.transaction.application.usecase;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserOutput;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class FindTransactionByUserUseCase implements UseCase<FindTransactionsByUserInput, FindTransactionsByUserOutput> {
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;

    public FindTransactionByUserUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public FindTransactionsByUserOutput execute(FindTransactionsByUserInput input) {
        adminChecker.check(input.principal());

        List<Transaction> transactions = transactionRepository.getByUserId(input.userId());

        return new FindTransactionsByUserOutput(transactions);
    }
}
