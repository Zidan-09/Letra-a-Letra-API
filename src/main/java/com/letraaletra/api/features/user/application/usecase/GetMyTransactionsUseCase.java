package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.GetMyTransactionsInput;
import com.letraaletra.api.features.user.application.output.GetMyTransactionsOutput;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetMyTransactionsUseCase implements UseCase<GetMyTransactionsInput, GetMyTransactionsOutput> {
    private final TransactionRepository transactionRepository;

    public GetMyTransactionsUseCase(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public GetMyTransactionsOutput execute(GetMyTransactionsInput input) {
        Page<TransactionDetails> transactions = transactionRepository.getByUserId(input.id(), new TransactionsPage(
                input.page(),
                input.size(),
                input.sort()
        ));

        return new GetMyTransactionsOutput(transactions);
    }
}
