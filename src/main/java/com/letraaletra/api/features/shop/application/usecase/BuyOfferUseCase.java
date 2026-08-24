package com.letraaletra.api.features.shop.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.offers.domain.exception.OfferAlreadyPurchasedException;
import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.InventoryMovement;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BuyOfferUseCase implements UseCase<BuyOfferInput, BuyOfferOutput> {
    private static final String SOURCE_DETAIL = "BUY_OFFER";

    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final TransactionRepository transactionRepository;
    private final BusinessAuditRecorder auditRecorder;

    public BuyOfferUseCase(
            UserRepository userRepository,
            OfferRepository offerRepository,
            TransactionRepository transactionRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.transactionRepository = transactionRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public BuyOfferOutput execute(BuyOfferInput input) {
        UUID operationId = UUID.randomUUID();

        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        User user = userRepository.find(input.auth())
                .orElseThrow(UserNotFoundException::new);

        validateOffer(offer, user.getUserId());

        processPayment(user, offer, operationId);

        userRepository.save(user);

        return new BuyOfferOutput(offer);
    }

    private void validateOffer(Offer offer, UUID userId) {
        if (!offer.isActive()) {
            throw new InvalidOfferStatusException();
        }

        if (offer.getCoinType().equals(CoinType.REAL)) {
            throw new InvalidPaymentException();
        }

        if (!offer.isRepeatable()
                && transactionRepository.existsOfferPurchase(userId, offer.getOfferId())) {
            throw new OfferAlreadyPurchasedException();
        }
    }

    private void processPayment(User user, Offer offer, UUID operationId) {
        WalletMovement walletMovement = user.getWallet()
                .remove(offer.getCoinType(), offer.getPrice().intValueExact());

        Transaction transaction = saveWalletTransaction(user, walletMovement, TransactionReason.SHOP_PURCHASE, offer.getOfferId());

        auditRecorder.record(AuditEventFactory.walletMovement(
                walletMovement,
                user.getUserId(),
                buyerActor(user),
                TransactionReason.SHOP_PURCHASE.name(),
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId,
                transaction.transactionId()
        ));

        processRewards(user, offer, operationId);
    }

    private void processRewards(User user, Offer offer, UUID operationId) {
        offer.getRewards().forEach(offerReward -> {
            Reward reward = offerReward.reward();

            Optional<WalletMovement> movement = reward.apply(user);

            movement.ifPresent(walletMovement -> {
                Transaction transaction = saveWalletTransaction(user, walletMovement, TransactionReason.SHOP_PURCHASE, offer.getOfferId());

                auditRecorder.record(AuditEventFactory.walletMovement(
                        walletMovement,
                        user.getUserId(),
                        buyerActor(user),
                        TransactionReason.SHOP_PURCHASE.name(),
                        AuditSourceType.HTTP,
                        SOURCE_DETAIL,
                        operationId,
                        transaction.transactionId()
                ));
            });

            List<InventoryMovement> inventoryMovements = reward.applyInventory(user);

            AuditEventFactory.inventoryChanges(
                    inventoryMovements,
                    user.getUserId(),
                    buyerActor(user),
                    TransactionReason.SHOP_PURCHASE.name(),
                    AuditSourceType.HTTP,
                    SOURCE_DETAIL,
                    operationId
            ).forEach(auditRecorder::record);
        });
    }

    private Transaction saveWalletTransaction(User user, WalletMovement movement, TransactionReason reason, UUID referenceId) {
        return transactionRepository.save(
                Transaction.create(
                        user.getUserId(),
                        movement.coinType(),
                        movement.amount(),
                        (int) movement.balanceBefore()
                                .getAmountFor(movement.coinType()),
                        (int) movement.balanceAfter()
                                .getAmountFor(movement.coinType()),
                        movement.operation(),
                        reason,
                        referenceId
                )
        );
    }

    private AuditActor buyerActor(User user) {
        return new AuditActor(AuditActorType.USER, user.getUserId(), user.getUsername());
    }
}
