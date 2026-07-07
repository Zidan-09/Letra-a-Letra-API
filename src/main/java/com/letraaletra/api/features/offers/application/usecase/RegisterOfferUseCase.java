package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;

public class RegisterOfferUseCase implements UseCase<RegisterOfferInput, RegisterOfferOutput> {
    private final OfferRepository offerRepository;
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public RegisterOfferUseCase(
            OfferRepository offerRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.offerRepository = offerRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public RegisterOfferOutput execute(RegisterOfferInput input) {
        adminChecker.check(input.auth());

        Cosmetic cosmetic = cosmeticRepository.find(input.cosmeticId()).orElseThrow(CosmeticNotFoundException::new);

        Offer offer = buildOffer(input, cosmetic);

        offerRepository.save(offer);

        return buildOutput(offer);
    }

    private Offer buildOffer(RegisterOfferInput input, Cosmetic cosmetic) {
        return Offer.create(
                input.title(),
                input.coinType(),
                input.price(),
                cosmetic,
                input.rewardSoftCoins(),
                input.rewardHardGems(),
                true,
                LocalDateTime.now().plusHours(input.expiresIn())
        );
    }

    private RegisterOfferOutput buildOutput(Offer offer) {
        return new RegisterOfferOutput(
                offer
        );
    }
}
