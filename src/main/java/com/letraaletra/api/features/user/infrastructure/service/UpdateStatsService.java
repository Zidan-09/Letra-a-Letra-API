package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.inventory.InventoryMovement;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateStatsService implements UserStatsService {
    private static final String SOURCE_DETAIL = "LEVEL_UP_REWARD";

    private final LevelRepository levelRepository;
    private final TransactionRepository walletTransactionRepository;
    private final BusinessAuditRecorder auditRecorder;
    private final OperationContext operationContext;

    @Override
    public void update(User user, boolean isWinner) {
        user.registerMatchResult(isWinner);

        int maxLevel = levelRepository.findBiggestLevel();

        int experience = isWinner ? 10 * 3 : 10;

        int beforeLevel = user.getStats().getLevel();

        user.getStats().incrementExperience(experience, maxLevel == 0 ? 1 : maxLevel);

        int afterLevel = user.getStats().getLevel();

        if (afterLevel > beforeLevel) {
            Optional<Level> level = levelRepository.findByLevel(afterLevel);

            level.ifPresent(l -> l.getRewards().forEach(levelReward ->
                    applyReward(user, l, levelReward.reward())
            ));
        }
    }

    private void applyReward(User user, Level level, Reward reward) {
        UUID operationId = operationContext.currentOperationId().orElseGet(UUID::randomUUID);

        Optional<WalletMovement> movement = reward.apply(user);

        movement.ifPresent(walletMovement -> {
            Transaction saved = walletTransactionRepository.save(
                    Transaction.create(
                            user.getUserId(),
                            walletMovement.coinType(),
                            walletMovement.amount(),
                            (int) walletMovement.balanceBefore()
                                    .getAmountFor(walletMovement.coinType()),
                            (int) walletMovement.balanceAfter()
                                    .getAmountFor(walletMovement.coinType()),
                            walletMovement.operation(),
                            TransactionReason.LEVEL_UP,
                            level.getLevelId()
                    )
            );

            auditRecorder.record(AuditEventFactory.walletMovement(
                    walletMovement,
                    user.getUserId(),
                    AuditActor.system(),
                    TransactionReason.LEVEL_UP.name(),
                    AuditSourceType.SYSTEM,
                    SOURCE_DETAIL,
                    operationId,
                    saved.transactionId()
            ));
        });

        List<InventoryMovement> inventoryMovements = reward.applyInventory(user);

        AuditEventFactory.inventoryChanges(
                inventoryMovements,
                user.getUserId(),
                AuditActor.system(),
                TransactionReason.LEVEL_UP.name(),
                AuditSourceType.SYSTEM,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);
    }
}
