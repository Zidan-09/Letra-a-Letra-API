package com.letraaletra.api.features.transaction.application.usecase;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserNicknameInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserNicknameOutput;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class FindTransactionsByUserNicknameUseCase implements
        UseCase<FindTransactionsByUserNicknameInput, FindTransactionsByUserNicknameOutput> {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;

    public FindTransactionsByUserNicknameUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public FindTransactionsByUserNicknameOutput execute(FindTransactionsByUserNicknameInput input) {
        adminChecker.check(input.principal());

        User user = userRepository.findByNickname(input.nickname())
                .orElseThrow(UserNotFoundException::new);

        Page<TransactionDetails> transactions = transactionRepository.getByUserId(user.getId(), new TransactionsPage(
                input.page(),
                input.size(),
                input.sort()
        ));

        return new FindTransactionsByUserNicknameOutput(transactions);
    }
}
