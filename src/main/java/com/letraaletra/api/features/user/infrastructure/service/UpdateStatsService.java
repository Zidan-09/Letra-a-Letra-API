package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.inventory.application.usecase.InventoryPersistence;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.effect.effects.CoinBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void update(User user, boolean isWinner) {
        user.registerMatchResult(isWinner);

        int maxLevel = levelRepository.findBiggestLevel();

        int experience = applyExperienceBonus(user, isWinner ? 10 * 3 : 10);

        int beforeLevel = user.getStats().getLevel();

        user.getStats().incrementExperience(experience, maxLevel == 0 ? 1 : maxLevel);

        user.getActiveEffects().updateEffects();

        int afterLevel = user.getStats().getLevel();

        if (afterLevel > beforeLevel) {
            Optional<Level> level = levelRepository.findByLevel(afterLevel);

            level.ifPresent(l -> l.getRewards().forEach(levelReward ->
                    applyReward(user, l, levelReward.reward())
            ));
        }
    }

    private int applyExperienceBonus(User user, int experience) {
        return user.getActiveEffects().find(ExperienceBonusEffect.class)
                .filter(effect -> effect.isValid(Instant.now()))
                .map(effect -> experience + experience * effect.getBonusPercentage() / 100)
                .orElse(experience);
    }

    private Reward applyCoinBonus(User user, Reward reward) {
        int bonus = user.getActiveEffects().find(CoinBonusEffect.class)
                .filter(effect -> effect.isValid(Instant.now()))
                .map(CoinBonusEffect::getBonusPercentage)
                .orElse(0);

        if (bonus <= 0) {
            return reward;
        }

        if (reward instanceof com.letraaletra.api.features.reward.domain.SoftCoinsReward soft) {
            return new com.letraaletra.api.features.reward.domain.SoftCoinsReward(
                    soft.amount() + soft.amount() * bonus / 100);
        }

        if (reward instanceof com.letraaletra.api.features.reward.domain.HardGemsReward hard) {
            return new com.letraaletra.api.features.reward.domain.HardGemsReward(
                    hard.amount() + hard.amount() * bonus / 100);
        }

        return reward;
    }

    private void applyReward(User user, Level level, Reward reward) {
        UUID operationId = operationContext.currentOperationId().orElseGet(UUID::randomUUID);

        if (reward instanceof ItemGrantReward itemReward) {
            grantItemReward(user, itemReward, operationId);
            return;
        }

        Reward boosted = applyCoinBonus(user, reward);

        Optional<WalletMovement> movement = boosted.apply(user);

        movement.ifPresent(walletMovement -> {
            Transaction saved = walletTransactionRepository.save(
                    Transaction.create(
                            user.getUserId(),
                            walletMovement.coinType(),
                            walletMovement.amount(),
                            walletMovement.balanceBefore()
                                    .getAmountFor(walletMovement.coinType()),
                            walletMovement.balanceAfter()
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
    }

    private void grantItemReward(User user, ItemGrantReward reward, UUID operationId) {
        Item item = itemRepository.findById(reward.definitionId())
                .orElseThrow(ItemNotFoundException::new);

        grantDefinition(user, item, reward.quantity(), operationId);
    }

    private void grantDefinition(User user, Item item, int quantity, UUID operationId) {
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
                AuditActor.system(),
                TransactionReason.LEVEL_UP.name(),
                AuditSourceType.SYSTEM,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);
    }
}
