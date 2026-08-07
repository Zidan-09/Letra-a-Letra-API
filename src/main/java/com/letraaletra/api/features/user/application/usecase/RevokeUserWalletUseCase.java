package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.RevokeUserWalletInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RevokeUserWalletUseCase implements UseCase<RevokeUserWalletInput, Void> {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;

    public RevokeUserWalletUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public Void execute(RevokeUserWalletInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        WalletMovement movement = user.getWallet().remove(input.type(), input.amount());

        Transaction transaction = Transaction.create(
                input.userId(),
                input.type(),
                input.amount(),
                (int) movement.balanceBefore()
                        .getAmountFor(input.type()),
                (int) movement.balanceAfter()
                        .getAmountFor(input.type()),
                OperationType.DEBIT,
                TransactionReason.ADMIN_REVOKE,
                input.principal().auth()
        );

        userRepository.save(user);
        transactionRepository.save(transaction);

        return null;
    }
}
