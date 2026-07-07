package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.DisableOfferInput;
import com.letraaletra.api.features.offers.application.output.DisableOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DisableOfferUseCase implements UseCase<DisableOfferInput, DisableOfferOutput> {
    private final OfferRepository offerRepository;
    private final AdminChecker adminChecker;

    public DisableOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker
    ) {
        this.offerRepository = offerRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public DisableOfferOutput execute(DisableOfferInput input) {
        adminChecker.check(input.auth());

        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        offer.disable();

        offerRepository.save(offer);

        return buildOutput(offer);
    }

    private DisableOfferOutput buildOutput(Offer offer) {
        return new DisableOfferOutput(offer);
    }
}
