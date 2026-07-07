package com.letraaletra.api.features.shop.application.usecase;

import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class BuyOfferUseCase implements UseCase<BuyOfferInput, BuyOfferOutput> {
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;

    public BuyOfferUseCase(
            UserRepository userRepository,
            OfferRepository offerRepository
    ) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
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
    }

    private void processPayment(User user, Offer offer) {
        user.getWallet().pay(offer.getCoinType(), offer.getPrice());

        user.getInventory().unlock(offer.getCosmetic());
    }
}
