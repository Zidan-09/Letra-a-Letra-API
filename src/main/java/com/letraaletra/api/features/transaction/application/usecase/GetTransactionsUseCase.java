package com.letraaletra.api.features.transaction.application.usecase;

import com.letraaletra.api.features.transaction.application.input.GetTransactionsInput;
import com.letraaletra.api.features.transaction.application.output.GetTransactionsOutput;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetTransactionsUseCase implements UseCase<GetTransactionsInput, GetTransactionsOutput> {
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;

    public GetTransactionsUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetTransactionsOutput execute(GetTransactionsInput input) {
        adminChecker.check(input.principal());

        Page<TransactionDetails> transactions = transactionRepository.get(
                new TransactionsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                )
        );

        return new GetTransactionsOutput(transactions);
    }
}
