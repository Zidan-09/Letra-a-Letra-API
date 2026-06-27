package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.store.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class BuyOfferUseCase implements UseCase<BuyOfferInput, BuyOfferOutput> {
    private final UserRepository userRepository;
    private final StoreOfferRepository storeOfferRepository;

    public BuyOfferUseCase(
            UserRepository userRepository,
            StoreOfferRepository storeOfferRepository
    ) {
        this.userRepository = userRepository;
        this.storeOfferRepository = storeOfferRepository;
    }

    @Override
    public BuyOfferOutput execute(BuyOfferInput input) {
        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        StoreOffer offer = storeOfferRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        validateOffer(offer);
        processPayment(user, offer);

        user.getInventory().unlock(offer.getCosmetic());

        userRepository.save(user);

        return new BuyOfferOutput(offer);
    }

    private void validateOffer(StoreOffer offer) {
        if (!offer.isActive()) {
            throw new InvalidOfferStatusException();
        }
    }

    private void processPayment(User user, StoreOffer offer) {
        user.getWallet().pay(offer.getCoinType(), offer.getPrice());
    }
}
