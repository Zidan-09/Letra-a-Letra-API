package com.letraaletra.api.features.shop.application.usecase;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.repository.WalletTransactionRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.TransactionReason;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.Optional;

public class BuyOfferUseCase implements UseCase<BuyOfferInput, BuyOfferOutput> {
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public BuyOfferUseCase(
            UserRepository userRepository,
            OfferRepository offerRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Override
    public BuyOfferOutput execute(BuyOfferInput input) {
        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        User user = userRepository.find(input.auth())
                .orElseThrow(UserNotFoundException::new);

        validateOffer(offer);
        processPayment(user, offer);

        userRepository.save(user);

        return new BuyOfferOutput(offer);
    }

    private void validateOffer(Offer offer) {
        if (!offer.isActive()) {
            throw new InvalidOfferStatusException();
        }

        if (offer.getCoinType().equals(CoinType.REAL)) {
            throw new InvalidPaymentException();
        }
    }

    private void processPayment(User user, Offer offer) {
        user.getWallet().pay(offer.getCoinType(), offer.getPrice());

        processRewards(user, offer);
    }

    private void processRewards(User user, Offer offer) {
        offer.getRewards().forEach(offerReward -> {
            Balance balanceBefore = user.getWallet().getBalance();

            Optional<WalletMovement> movement = offerReward.reward().deliver(user);

            movement.ifPresent(walletMovement -> walletTransactionRepository.save(
                    WalletTransaction.create(
                            user.getId(),
                            walletMovement.coinType(),
                            walletMovement.amount(),
                            getBalance(balanceBefore, walletMovement.coinType()),
                            getBalance(user.getWallet().getBalance(), walletMovement.coinType()),
                            walletMovement.operation(),
                            TransactionReason.SHOP_PURCHASE,
                            offer.getOfferId()
                    )
            ));
        });
    }

    private int getBalance(Balance balance, CoinType coinType) {
        return switch (coinType) {
            case SOFT -> (int) balance.coins();
            case HARD -> (int) balance.gems();
            case REAL -> throw new IllegalStateException(
                    "Wallet movements cannot use REAL coin type."
            );
        };
    }
}
