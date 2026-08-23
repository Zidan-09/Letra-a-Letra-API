package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.InventoryMovement;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.features.reward.domain.Reward;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GrantUserRewardUseCase implements UseCase<GrantUserRewardInput, Void> {
    private static final String SOURCE_DETAIL = "GRANT_USER_REWARD";

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;
    private final RewardFactory rewardFactory;
    private final BusinessAuditRecorder auditRecorder;

    public GrantUserRewardUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory,
            BusinessAuditRecorder auditRecorder
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
        this.rewardFactory = rewardFactory;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public Void execute(GrantUserRewardInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        UUID operationId = UUID.randomUUID();
        AuditActor actor = adminActor(input.principal());

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        Reward reward = rewardFactory.create(
                input.rewardType(),
                input.amount(),
                input.cosmeticId()
        );

        grantWalletReward(user, reward, actor, operationId);
        grantInventoryReward(user, reward, actor, operationId);

        userRepository.save(user);

        return null;
    }

    private void grantWalletReward(User user, Reward reward, AuditActor actor, UUID operationId) {
        Optional<WalletMovement> movement = reward.apply(user);

        movement.ifPresent(walletMovement -> {
            Transaction transaction = transactionRepository.save(Transaction.create(
                    user.getUserId(),
                    walletMovement.coinType(),
                    walletMovement.amount(),
                    (int) walletMovement.balanceBefore()
                            .getAmountFor(walletMovement.coinType()),
                    (int) walletMovement.balanceAfter()
                            .getAmountFor(walletMovement.coinType()),
                    walletMovement.operation(),
                    TransactionReason.ADMIN_GIVE,
                    actor.id()
            ));

            auditRecorder.record(AuditEventFactory.walletMovement(
                    walletMovement,
                    user.getUserId(),
                    actor,
                    TransactionReason.ADMIN_GIVE.name(),
                    AuditSourceType.HTTP,
                    SOURCE_DETAIL,
                    operationId,
                    transaction.transactionId()
            ));
        });
    }

    private void grantInventoryReward(User user, Reward reward, AuditActor actor, UUID operationId) {
        List<InventoryMovement> movements = reward.applyInventory(user);

        AuditEventFactory.inventoryChanges(
                movements,
                user.getUserId(),
                actor,
                TransactionReason.ADMIN_GIVE.name(),
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);
    }

    private AuditActor adminActor(AuthenticatedUser principal) {
        return new AuditActor(AuditActorType.ADMIN, principal.auth(), principal.name());
    }
}
