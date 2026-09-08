package com.letraaletra.api.features.shop.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.shop.application.port.ShopPurchasePort;
import com.letraaletra.api.features.shop.application.port.ShopPurchaseResult;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.InventoryChangeKind;
import com.letraaletra.api.features.user.domain.inventory.InventoryMovement;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuyOfferUseCase implements UseCase<BuyOfferInput, BuyOfferOutput> {
    private static final String SOURCE_DETAIL = "BUY_OFFER";

    private final ShopPurchasePort purchasePort;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final BusinessAuditRecorder auditRecorder;

    public BuyOfferUseCase(
            ShopPurchasePort purchasePort,
            OfferRepository offerRepository,
            UserRepository userRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.purchasePort = purchasePort;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public BuyOfferOutput execute(BuyOfferInput input) {
        UUID operationId = UUID.randomUUID();

        ShopPurchaseResult result = purchasePort.purchase(input.auth(), input.offerId());

        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        User user = userRepository.find(input.auth())
                .orElseThrow(UserNotFoundException::new);

        recordAudit(user, offer, result, operationId);

        return new BuyOfferOutput(offer);
    }

    private void recordAudit(User user, Offer offer, ShopPurchaseResult result, UUID operationId) {
        AuditActor actor = buyerActor(user);
        List<UUID> transactionIds = result.transactionIds();
        UUID debitTransactionId = transactionIds.isEmpty() ? null : transactionIds.get(0);

        int price = offer.getPrice().intValueExact();

        long softRewards = 0L;
        long hardRewards = 0L;
        for (OfferReward offerReward : offer.getRewards()) {
            if (offerReward.reward() instanceof SoftCoinsReward soft) {
                softRewards += soft.amount();
            } else if (offerReward.reward() instanceof HardGemsReward hard) {
                hardRewards += hard.amount();
            }
        }

        long currentSoft = result.newSoftCoins()
                + (offer.getCoinType().equals(CoinType.SOFT) ? price : 0)
                - softRewards;
        long currentHard = result.newHardGems()
                + (offer.getCoinType().equals(CoinType.HARD) ? price : 0)
                - hardRewards;

        Balance debitBefore = new Balance(currentSoft, currentHard);
        if (offer.getCoinType().equals(CoinType.SOFT)) {
            currentSoft -= price;
        } else {
            currentHard -= price;
        }
        Balance debitAfter = new Balance(currentSoft, currentHard);

        WalletMovement debit = new WalletMovement(
                offer.getCoinType(),
                debitBefore,
                debitAfter,
                price,
                OperationType.DEBIT
        );

        auditRecorder.record(AuditEventFactory.walletMovement(
                debit,
                user.getUserId(),
                actor,
                TransactionReason.SHOP_PURCHASE.name(),
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId,
                debitTransactionId
        ));

        int transactionIndex = 1;
        for (OfferReward offerReward : offer.getRewards()) {
            if (offerReward.reward() instanceof SoftCoinsReward soft) {
                Balance before = new Balance(currentSoft, currentHard);
                currentSoft += soft.amount();
                Balance after = new Balance(currentSoft, currentHard);
                WalletMovement movement = new WalletMovement(
                        CoinType.SOFT,
                        before,
                        after,
                        soft.amount(),
                        OperationType.CREDIT
                );
                auditRecorder.record(AuditEventFactory.walletMovement(
                        movement,
                        user.getUserId(),
                        actor,
                        TransactionReason.SHOP_PURCHASE.name(),
                        AuditSourceType.HTTP,
                        SOURCE_DETAIL,
                        operationId,
                        transactionIdAt(transactionIds, transactionIndex++)
                ));
            } else if (offerReward.reward() instanceof HardGemsReward hard) {
                Balance before = new Balance(currentSoft, currentHard);
                currentHard += hard.amount();
                Balance after = new Balance(currentSoft, currentHard);
                WalletMovement movement = new WalletMovement(
                        CoinType.HARD,
                        before,
                        after,
                        hard.amount(),
                        OperationType.CREDIT
                );
                auditRecorder.record(AuditEventFactory.walletMovement(
                        movement,
                        user.getUserId(),
                        actor,
                        TransactionReason.SHOP_PURCHASE.name(),
                        AuditSourceType.HTTP,
                        SOURCE_DETAIL,
                        operationId,
                        transactionIdAt(transactionIds, transactionIndex++)
                ));
            }
        }

        List<InventoryMovement> inventoryMovements = new ArrayList<>();
        for (OfferReward offerReward : offer.getRewards()) {
            if (offerReward.reward() instanceof CosmeticReward cosmetic) {
                inventoryMovements.add(new InventoryMovement(
                        cosmetic.cosmetic().getId(),
                        InventoryChangeKind.ACQUIRED,
                        null,
                        false
                ));
            }
        }

        AuditEventFactory.inventoryChanges(
                inventoryMovements,
                user.getUserId(),
                actor,
                TransactionReason.SHOP_PURCHASE.name(),
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);
    }

    private UUID transactionIdAt(List<UUID> transactionIds, int index) {
        if (index < transactionIds.size()) {
            return transactionIds.get(index);
        }
        return null;
    }

    private AuditActor buyerActor(User user) {
        return new AuditActor(AuditActorType.USER, user.getUserId(), user.getUsername());
    }
}
