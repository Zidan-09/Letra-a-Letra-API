package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.inventory.application.usecase.InventoryPersistence;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
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
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final BusinessAuditRecorder auditRecorder;

    public GrantUserRewardUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory,
            ItemRepository itemRepository,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
        this.rewardFactory = rewardFactory;
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
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
                input.quantity(),
                input.referenceId()
        );

        grantInventoryReward(user, reward, actor, operationId);
        grantWalletReward(user, reward, actor, operationId);

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
                    walletMovement.balanceBefore()
                            .getAmountFor(walletMovement.coinType()),
                    walletMovement.balanceAfter()
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
        if (reward instanceof ItemGrantReward itemReward) {
            grantItemReward(user, itemReward, actor, operationId);
        }
    }

    private void grantItemReward(User user, ItemGrantReward reward, AuditActor actor, UUID operationId) {
        Item item = itemRepository.findById(reward.definitionId())
                .orElseThrow(ItemNotFoundException::new);

        grantDefinition(user, item, reward.quantity(), actor, operationId);
    }

    private void grantDefinition(User user, Item item, int quantity, AuditActor actor, UUID operationId) {
        Inventory inventory = Inventory.restore(
                user.getUserId(),
                inventoryRepository.findItemsByOwner(user.getUserId())
        );

        List<com.letraaletra.api.features.inventory.domain.InventoryMovement> movements =
                inventory.grant(item, quantity);

        InventoryPersistence.save(inventoryRepository, user.getUserId(), inventory);

        AuditEventFactory.itemChanges(
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
