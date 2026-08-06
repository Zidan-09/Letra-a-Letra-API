package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.rewards.Reward;

public class GrantUserRewardUseCase implements UseCase<GrantUserRewardInput, Void> {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;
    private final RewardFactory rewardFactory;

    public GrantUserRewardUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
        this.rewardFactory = rewardFactory;
    }

    @Override
    public Void execute(GrantUserRewardInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        Reward reward = rewardFactory.create(
                input.rewardType(),
                input.amount(),
                input.cosmeticId()
        );

        reward.apply(user)
                .ifPresent(walletMovement -> saveAdminGrantTransaction(walletMovement, user));

        userRepository.save(user);

        return null;
    }

    private void saveAdminGrantTransaction(WalletMovement walletMovement, User user) {
        transactionRepository.save(Transaction.create(
                user.getUserId(),
                walletMovement.coinType(),
                walletMovement.amount(),
                (int) walletMovement.balanceBefore()
                        .getAmountFor(walletMovement.coinType()),
                (int) walletMovement.balanceAfter()
                        .getAmountFor(walletMovement.coinType()),
                walletMovement.operation(),
                TransactionReason.ADMIN_GIVE,
                null
        ));
    }
}
