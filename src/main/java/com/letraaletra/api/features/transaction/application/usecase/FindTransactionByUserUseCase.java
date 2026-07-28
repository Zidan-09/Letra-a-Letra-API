package com.letraaletra.api.features.transaction.application.usecase;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserOutput;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

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

        Page<TransactionDetails> transactions = transactionRepository.getByUserId(input.userId(), new TransactionsPage(
               input.page(),
               input.size(),
               input.sort()
        ));

        return new FindTransactionsByUserOutput(transactions);
    }
}
